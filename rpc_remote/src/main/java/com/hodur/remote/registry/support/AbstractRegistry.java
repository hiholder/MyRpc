package com.hodur.remote.registry.support;

import com.hodur.common.Constants;
import com.hodur.common.URL;
import com.hodur.remote.registry.api.Registry;
import com.hodur.common.utils.concurrent.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

/**
 * @author Hodur
 * @className AbstrractRegistry.java
 * @description
 * @date 2021年11月28日 18:10
 */
@Slf4j
public abstract class AbstractRegistry implements Registry {
    // URL的地址分隔符，在缓存文件中使用，服务提供者的URL分隔
    private static final char URL_SEPARATOR = ' ';
    // URL地址分隔正则表达式，用于解析文件缓存中服务提供者URL列表
    private static final String URL_SPLIT = "\\s+";
    // 本地磁盘缓存，有一个特殊的key值为registies，记录的是注册中心列表，其他记录的都是服务提供者列表
    private final Properties properties = new Properties();
    // 已注册 服务URL 集合
    // 注册的 URL 不仅仅可以是服务提供者的，也可以是服务消费者的
    private final Set<URL> registered = new ConcurrentHashSet<URL>();
    // 注册中心 URL
    private URL registryUrl;
    // 本地磁盘缓存文件，缓存注册中心的数据
    private File file;
    public AbstractRegistry(URL url) {
        setUrl(url);
        String filename = url.getParameter(Constants.FILE_KEY, System.getProperty("user.home")+"/.rpc/rpc-registry-"+url.getParameter(Constants.APPLICATION_KEY)+"-"+url.getAddress()+".cache");
        File file = null;
        if (filename == null || filename.length() == 0) {
            file = new File(filename);
            if (!file.exists() && file.getParentFile() != null && !file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    throw new IllegalArgumentException("Invalid registry store file " + file + ", cause: Failed to create directory " + file.getParentFile() + "!");
                }
            }
        }
        this.file = file;
        loadProperties();
    }
    protected void setUrl(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("registry url == null");
        }
        this.registryUrl = url;
    }
    public Set<URL> getRegistered() {
        return registered;
    }
    /**
     * @describe 加载本地磁盘缓存文件到内存，也就是把文件里面的数据写入properties
     * @author Hodur
     * @date 2021/12/1 10:24
     */
    private void loadProperties() {
        if (file != null && file.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                // 把数据写入内存缓存中
                properties.load(in);
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public List<URL> getCacheUrls(URL url) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            // key为某个分类，例如服务提供者分类
            String key = (String) entry.getKey();
            // value为某个分类列表，例如服务提供者列表
            String value = (String) entry.getValue();
            if (key != null && key.length() != 0 && value != null && value.length() != 0) {
                String[] arr = value.trim().split(URL_SPLIT);
                List<URL> urls = new ArrayList<>();
                for (String u : arr) {
                    urls.add(URL.valueOf(u));
                }
                return urls;
            }
        }
        return null;
    }
    /**
     * @describe 服务注册
     * @author Hodur
     * @date 2021/12/3 8:59
     * @param url
     */
    @Override
    public void register(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("register url == null");
        }
        log.info("Register: "+url);
        registered.add(url);
    }
    /**
     * @describe 取消服务注册
     * @author Hodur
     * @date 2021/12/3 8:59
     * @param url
     */
    @Override
    public void unregister(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("unregister url == null");
        }
        log.info("Unregister: "+url);
        registered.remove(url);
    }
    /**
     * @describe 服务发现
     * @author Hodur
     * @date 2021/12/3 8:59
     * @param url
     * @return java.util.List<com.hodur.common.URL>
     */
    @Override
    public List<URL> lookup(URL url) {

        return null;
    }

    /**
     * @describe 注册中心连接断开后恢复
     * @author Hodur
     * @date 2021/12/3 9:01
     */
    protected void recover() {
        // register
        // 通过缓存中的registered中的内容重新注册
        Set<URL> recoverRegistered = new HashSet<>(getRegistered());
        if (!recoverRegistered.isEmpty()) {
            log.info("Recover register url" + recoverRegistered);
            for (URL url : recoverRegistered) {
                register(url);
            }
        }

    }

    @Override
    public URL getUrl() {
        return registryUrl;
    }

    /**
     * @describe 当JVM关闭时，取消注册
     * @author Hodur
     * @date 2021/12/3 10:28
     */
    @Override
    public void destroy() {
        log.info("Destroy registry: " + getUrl());
        Set<URL> destroyRegistered = new HashSet<URL>(getRegistered());
        if (!destroyRegistered.isEmpty()) {
            for (URL url : destroyRegistered) {
                if (url.getParameter(Constants.DYNAMIC_KEY, true)) {
                    try {
                        unregister(url);
                        log.info("Destroy unregister url " + url);
                    } catch (Throwable t) {
                        log.warn("Failed to unregister url " + url + "to registry " + getUrl() + "on destroy, cause: " + t.getMessage(), t);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return getUrl().toString();
    }
}
