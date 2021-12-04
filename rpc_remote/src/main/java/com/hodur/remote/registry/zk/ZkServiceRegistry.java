package com.hodur.remote.registry.zk;

import com.hodur.common.Constants;
import com.hodur.common.URL;
import com.hodur.remote.registry.support.FailbackRegistry;
import com.hodur.remote.registry.zk.util.CuratorUtils;
import com.hodur.common.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hodur
 * @className ZkServiceRegistry.java
 * @description
 * @date 2021年04月15日 9:34
 */
@Slf4j

public class ZkServiceRegistry extends FailbackRegistry   {
    // 默认zookeeper根节点
    private final static String DEFAULT_ROOT = "my-rpc";

    // zookeeper根节点
    private final String root;
    private  CuratorFramework zkClient;
    public ZkServiceRegistry(URL url) {
        super(url);
        // 获得url携带的分组配置
        String group = url.getGroup(DEFAULT_ROOT);
        if (!group.startsWith(Constants.PATH_SEPARATOR)) {
            group = Constants.PATH_SEPARATOR + group;
        }
        this.root = group;
        zkClient = CuratorUtils.getZkClient();
    }


    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH +"/"+ rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient,servicePath);
    }

    /**
     * @describe
     * @author Hodur
     * @date \ 8:43
     * @param url
     */
    @Override
    protected void doRegister(URL url) {
        if (zkClient == null) {
            zkClient = CuratorUtils.getZkClient();
        }
        CuratorUtils.createNode(zkClient, toUrlPath(url));
    }

    @Override
    protected void doUnregister(URL url) {
        CuratorUtils.delete(zkClient, toUrlPath(url));
    }
    /**
     * @describe
     * @author Hodur
     * @date 2021/12/2 8:43
     * @param url
     * @return java.util.List<com.hodur.common.URL>
     */
    @Override
    public List<URL> lookup(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("lookup url == null");
        }
        if (zkClient == null) {
            zkClient = CuratorUtils.getZkClient();
        }
        try {
            List<String> providers = new ArrayList<>();
            // 遍历分组类别
            String[] paths = toCategoriesPath(url);
            for (String path : paths) {
                // 获得子节点
                List<String> children = CuratorUtils.getChildren(zkClient, path);
                if (children != null) {
                    providers.addAll(children);
                }
            }
            return toUrlsWithoutEmpty(url, providers);
        } catch (Exception e) {
            throw new IllegalStateException("");
        }
    }
    /**
     * 获得分类路径，分类路径拼接规则：Root + Service + Type
     * @param url
     * @return
     */
    private String toCategoryPath(URL url) {
        return toServicePath(url) + Constants.PATH_SEPARATOR + url.getParameter(Constants.CATEGORY_KEY, Constants.DEFAULT_CATEGORY);
    }
    /**
     * 获得URL路径，拼接规则是Root + Service + Type + URL
     * @param url
     * @return
     */
    private String toUrlPath(URL url) {
        // todo: url.toString()->URL.encode(url.toFullString()), Dubbo中会将用户名和密码拼入url中
        return toCategoryPath(url) + Constants.PATH_SEPARATOR + URL.encode(url.toString());
    }

    /**
     * 获得服务路径，拼接规则：Root + ServiceName
     * @param url
     * @return
     */
    private String toServicePath(URL url) {
        String name = url.getServiceInterface();
        // 如果是包括所有服务，则返回根节点
        return toRootDir() + URL.encode(name);
    }
    private String toRootDir() {
        if (root.equals(Constants.PATH_SEPARATOR)) {
            return root;
        }
        return root + Constants.PATH_SEPARATOR;
    }

    /**
     * @describe 获得分类数组
     * @author Hodur
     * @date 2021/12/2 8:46
     * @param url
     * @return java.lang.String[]
     */
    private String[] toCategoriesPath(URL url) {
        String[] categories;
        // 如果url携带的分类配置为*，则创建包含所有分类的数组
        categories = url.getParameter(Constants.CATEGORY_KEY, new String[]{Constants.DEFAULT_CATEGORY});
        String[] paths = new String[categories.length];
        for (int i = 0; i < categories.length; i++) {
            // 加上服务路径
            paths[i] = toServicePath(url) + Constants.PATH_SEPARATOR + categories[i];
        }
        return paths;
    }

    private List<URL> toUrlsWithoutEmpty(URL consumer, List<String> providers) {
        List<URL> urls = new ArrayList<URL>();
        if (providers != null && !providers.isEmpty()) {
            // 遍历服务提供者
            for (String provider : providers) {
                provider = URL.decode(provider);
                if (provider.contains("://")) {
                    URL url = URL.valueOf(provider);
                    // 判断是否匹配，如果匹配，则加入到集合中
                    if (UrlUtils.isMatch(consumer, url)) {
                        urls.add(url);
                    }
                }
            }
        }
        return urls;
    }

    @Override
    public boolean isAvailable() {
        return CuratorUtils.isConnected(zkClient);
    }

    public void destroy() {
        super.destroy();
        try {
            zkClient.close();
        } catch (Exception e) {
            log.warn("Failed to close zookeeper client " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }
}
