package com.hodur.registry.zk.util;

import com.hodur.enums.RpcConfigEnum;
import com.hodur.utils.Assert;
import com.hodur.utils.PropertiesFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author Hodur
 * @className CuratorUtils.java
 * @description
 * @date 2021年04月14日 19:58
 */
@Slf4j
public class CuratorUtils {
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "localhost:2181";

    private CuratorUtils() {}
    /**
     * @describe 创建持久化节点
     * @author Hodur
     * @date 2021/4/14 20:29
     * @param zkClient
     * @param path
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) !=null) {
                log.info("The node already exist. The node is:[{}]",path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("The node was created successfully. The node is:[{}]" ,path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail",path);
        }
    }

    public static void createNode(CuratorFramework zkClient, String path) {
        if (checkExists(path)) {
            return;
        }
        int i = path.lastIndexOf('/');
        if (i > 0) {
            // 创建客户端
            createNode(zkClient, path.substring(0, i));
        }
        // 递归创建节点
        try {
            zkClient.create().forPath(path);
        } catch (KeeperException.NodeExistsException e) {
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static boolean checkExists(String path) {
        try {
            if (zkClient.checkExists().forPath(path) != null) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
    /**
     * @describe 获得子节点
     * @author Hodur
     * @date 2021/4/14 20:41
     * @param zkClient
     * @param rpcServiceName
     * @return java.util.List<java.lang.String>
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> result = null;
        String servicePath = ZK_REGISTER_ROOT_PATH+"/"+rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName,result);

        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail",servicePath);
        }
        return result;
    }

    public static List<String> getChildren(CuratorFramework zkClient, String path) {
        if (SERVICE_ADDRESS_MAP.containsKey(path)) {
            return SERVICE_ADDRESS_MAP.get(path);
        }
        List<String> result = null;
        try {
            result = zkClient.getChildren().forPath(path);
            SERVICE_ADDRESS_MAP.put(path,result);

        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail", path);
        }
        return result;
    }

    public static void delete(CuratorFramework zkClient,String path) {
        try {
            Assert.notNull(zkClient, new IllegalStateException("Zookeeper is not connected yet!"));
            zkClient.delete().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress) {
        REGISTERED_PATH_SET.stream().parallel().forEach(p-> {

                try {
                    if (p.endsWith(inetSocketAddress.toString())) {
                        zkClient.delete().forPath(p);
                    }
                } catch (Exception e) {
                    log.error("clear registry for path [{}] fail",REGISTERED_PATH_SET.toString());
                }

        });
        log.info("All registered services on the server are cleared:[{}]",REGISTERED_PATH_SET.toString());
    }

    public static CuratorFramework getZkClient() {
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        String zookeeperAddress = properties != null && properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue())
                != null ? properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) : DEFAULT_ZOOKEEPER_ADDRESS;
        //如果zkClient已经开始，之间返回
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        //放弃策略，放弃3次，每次放弃增加等待时间
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME,MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        try {
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)){
                throw new RuntimeException("Time out waiting to connect to ZK");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }
    private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient,servicePath,true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework,pathChildrenCacheEvent)-> {
            List<String> serviceAddress = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName,serviceAddress);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }
}
