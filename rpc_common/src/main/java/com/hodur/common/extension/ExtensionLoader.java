package com.hodur.common.extension;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * refer to dubbo spi: https://dubbo.apache.org/zh-cn/docs/source_code_guide/dubbo-spi.html
 */
@Slf4j
public final class ExtensionLoader<T> {

    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    private static final Pattern NAME_SEPARATOR = Pattern.compile("\\s*[,]+\\s*");
    private final Class<?> type;
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();
    //缓存的默认扩展名，就是@SPI中设置的值
    private String cachedDefaultName;

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface.");
        }
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        // firstly get from cache, if not hit, create one
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }

    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }
        //查找默认的扩展实现，也就是@SPI中的默认值作为key
        if ("true".equals(name)) {
            return getDefaultExtension();
        }
        // 从缓存中获取对应的拓展对象
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        // create a singleton if no instance exists
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    // 通过拓展名创建接口实现类的对象
                    instance = createExtension(name);
                    // 把创建的拓展对象放入缓存
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    private T createExtension(String name) {
        // 获取拓展名对应的拓展实现类
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new RuntimeException("No such extension of name " + name);
        }
        // 缓存是否有该类对象
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return instance;
    }

    private Map<String, Class<?>> getExtensionClasses() {
        // 从缓存中获取已加载的拓展类，单例模式
        Map<String, Class<?>> classes = cachedClasses.get();
        // double check
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    // load all extensions from our extensions directory
                    classes = loadExtensionClasses();
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }
    /**
     * @describe 从配置文件中，加载拓展实现类数组
     * @author Hodur
     * @date 2021/11/21 16:22
     * @return java.util.Map<java.lang.String,java.lang.Class<?>>
     */
    private Map<String, Class<?>> loadExtensionClasses() {
        final SPI defaultAnnotation = type.getAnnotation(SPI.class);
        // 加载SPI中的默认类型
        if (defaultAnnotation != null) {
            String value = defaultAnnotation.value();
            if ((value = value.trim()).length() > 0) {
                String[] names = NAME_SEPARATOR.split(value);
                // 只允许有一个默认值
                if (names.length > 1) {
                    throw new IllegalStateException("more than 1 default extension name on extension " + type.getName()
                            + ": " + Arrays.toString(names));
                }
                if (names.length == 1) {
                    cachedDefaultName = names[0];
                }
            }


        }
        Map<String, Class<?>> extensionClasses = new HashMap<String, Class<?>>();
        loadDirectory(extensionClasses);
        return extensionClasses;
    }
    /**
     * @describe 从一个配置文件中，加载拓展实现类数组
     * @author Hodur
     * @date 2021/4/21 15:41
     * @param extensionClasses
     */
    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        //拼接接口全限定名，得到完整数组
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);
            if (urls != null) {
                //遍历文件
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    /**
     * @describe 加载文件中的内容
     * @author Hodur
     * @date 2021/4/21 15:38
     * @param extensionClasses
     * @param classLoader
     * @param resourceUrl
     */
    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8));
            try {
                String line;
                // read every line
                while ((line = reader.readLine()) != null) {
                    // get index of comment
                    final int ci = line.indexOf('#');
                    if (ci >= 0) {
                        // string after # is comment so we ignore it
                        line = line.substring(0, ci);
                    }
                    line = line.trim();
                    if (line.length() > 0) {
                        try {
                            final int ei = line.indexOf('=');
                            String name = line.substring(0, ei).trim();
                            String clazzName = line.substring(ei + 1).trim();
                            // our SPI use key-value pair so both of them must not be empty
                            if (name.length() > 0 && clazzName.length() > 0) {
                                Class<?> clazz = classLoader.loadClass(clazzName);
                                extensionClasses.put(name, clazz);
                            }
                        } catch (ClassNotFoundException e) {
                            log.error(e.getMessage());
                        }
                    }

                }
            } finally {
                reader.close();
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * @describe 查找默认拓展的实现
     * @author Hodur
     * @date 2021/11/21 16:17
     * @return T
     */
    public T getDefaultExtension() {
        getExtensionClasses();
        if (null == cachedDefaultName || cachedDefaultName.length() == 0 || "true".equals(cachedDefaultName)) {
            return null;
        }
        return getExtension(cachedDefaultName);
    }

    public String getDefaultExtensionName() {
        getExtensionClasses();
        return cachedDefaultName;
    }
}
