package com.hodur;

import java.util.regex.Pattern;

/**
 * @author Hodur
 * @className Constants.java
 * @description
 * @date 2021年12月01日 10:00
 */
public class Constants {

    public static final String FILE_KEY = "file";

    public static final String DEFAULT_KEY_PREFIX = "default.";

    public static final String APPLICATION_KEY = "application";

    public final static String PATH_SEPARATOR = "/";

    public static final String CATEGORY_KEY = "category";

    public static final String PROVIDERS_CATEGORY = "providers";

    public static final String DEFAULT_CATEGORY = PROVIDERS_CATEGORY;

    // dubbo分组配置，例如因为服务器有限，想在同一个注册中心中，分隔测试和开发环境

    public static final String INTERFACE_KEY = "interface";

    public static final Pattern COMMA_SPLIT_PATTERN = Pattern
            .compile("\\s*[,]+\\s*");

    public static final String ANY_VALUE = "*";

    public static final String REMOVE_VALUE_PREFIX = "-";

    public static final String ENABLED_KEY = "enabled";

    // dubbo分组配置，例如因为服务器有限，想在同一个注册中心中，分隔测试和开发环境
    public static final String GROUP_KEY = "group";

    public static final String VERSION_KEY = "version";

    public static final String CLASSIFIER_KEY = "classifier";

}
