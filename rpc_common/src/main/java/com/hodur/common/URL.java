package com.hodur.common;

import lombok.*;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hodur
 * @className RpcServiceProperties.java
 * @description
 * @date 2021年04月13日 18:16
 */
@Getter
@Setter
public class URL {
    private String version;//版本

    private String group;//当接口类很多时，按照组号区分

    private String serviceName;//服务名

    private String protocol;

    private String path;
    // by default, host to registry
    private final String host;

    // by default, port to registry
    private final int port;
    private final Map<String, String> parameters;

    // ===== cache =====

    private volatile transient Map<String, Number> numbers;

    private volatile transient String string;

    private volatile transient String ip;



    protected URL() {
        this.protocol = null;
        this.host = null;
        this.port = 0;
        this.parameters = null;
    }
    public URL(String group, String version) {
        this.group = group;
        this.version = version;
        this.host = null;
        this.port = 0;
        this.parameters = null;
    }
    public URL(String protocol, String host, int port, String path, Map<String, String> parameters) {
        this.protocol = protocol;
        this.host = host;
        this.port = (port < 0 ? 0 : port);
        while (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        this.path = path;
        if (parameters == null) {
            parameters = new HashMap<String, String>();
        } else {
            parameters = new HashMap<String, String>(parameters);
        }
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    public String getGroup(String defaultValue) {
        if (group == null || group.length() == 0) {
            return defaultValue;
        }
        return group;
    }

    public String toRpcServiceName() {

        return this.getServiceName() + this.getGroup() + this.getVersion() + this.getProtocol()+this.getParameters();

    }

    public String getParameter(String key) {
        String value = parameters.get(key);
        if (value == null || value.length() == 0) {
            value = parameters.get(Constants.DEFAULT_KEY_PREFIX + key);
        }
        return value;
    }
    public String getParameter(String key, String defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value;
    }
    public String[] getParameter(String key, String[] defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return Constants.COMMA_SPLIT_PATTERN.split(value);
    }

    public boolean getParameter(String key, boolean defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    public int getParameter(String key, int defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.intValue();
        }
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        int i = Integer.parseInt(value);
        getNumbers().put(key, i);
        return i;
    }

    public String getAddress() {
        return port <= 0 ? host : host + ":" + port;
    }

    public static URL valueOf(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String protocol = null;
        String host = null;
        String path = null;
        int port = 0;
        Map<String, String> parameters = null;
        int i = url.indexOf("?"); // 分割请求体和参数
        if (i >= 0) {
            String[] params = url.substring(i+1).split("\\&");
            parameters = new HashMap<String, String>();
            for (String param : params) {
                param = param.trim();
                if (param.length() > 0) {
                    int j = param.indexOf('=');
                    if (j > 0) {
                        parameters.put(param.substring(0, j), param.substring(j+1));
                    } else {
                        parameters.put(param, param);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://"); // 处理url开头的协议部分
        if (i >= 0) {
            protocol = url.substring(0, i); // 获取协议
            url = url.substring(i+3);
        } else {
            // 处理 file:/path/to/file.txt 的情况
            i = url.indexOf(":/");
            if (i >= 0) {
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }
        i = url.indexOf("/");
        if (i >= 0) {
            path = url.substring(i+1);
            url = url.substring(0, i);
        }
        i = url.indexOf(":"); // 获取端口号
        if (i >= 0) {
            port = Integer.parseInt(url.substring(i+1));
            url = url.substring(0, i);
        }
        if (url.length() > 0) host = url;
        return new URL(protocol, host, port, path, parameters);
    }

    public URL addParameterIfAbsent(String key, String value) {
        if (key == null || key.length() == 0
                || value == null || value.length() == 0) {
            return this;
        }
        if (hasParameter(key)) {
            return this;
        }
        Map<String, String> map = new HashMap<String, String>(getParameters());
        map.put(key, value);
        return new URL(protocol, host, port, path, map);
    }

    public boolean hasParameter(String key) {
        String value = getParameter(key);
        return value != null && value.length() > 0;
    }

    @Override
    public String toString() {
        if (string != null) {
            return string;
        }
        return string = buildString(true, false, null);
    }

    private String buildString(boolean appendParameter, boolean useIP, String... parameters) {
        StringBuilder buf = new StringBuilder();
        if (protocol != null && protocol.length() != 0) {
            buf.append(protocol);
            buf.append("://");
        }
        String host;
        if (useIP) {
            host = getIp();
        } else {
            host = getHost();
        }
        if (host != null && host.length() > 0) {
            buf.append(host);
            if (port > 0) {
                buf.append(":");
                buf.append(port);
            }
        }
        String path = getPath();
        if (path != null && path.length() > 0) {
            buf.append("/");
            buf.append(path);
        }
        buildParameters(buf, parameters);
        return buf.toString();
    }

    private void buildParameters(StringBuilder buf, String[] parameters) {
        if (getParameters() != null && getParameters().size() > 0) {
            List<String> includes = (parameters == null || parameters.length == 0 ? null : Arrays.asList(parameters));
            boolean first = true;
            for (Map.Entry<String, String> entry : new TreeMap<String, String>(getParameters()).entrySet()) {
                if (entry.getKey() != null && entry.getKey().length() > 0
                    && (includes == null || includes.contains(entry.getKey()))) {
                    if (first) {
                        buf.append("?");
                        first = false;
                    } else {
                        buf.append("&");
                    }
                    buf.append(entry.getKey());
                    buf.append("=");
                    buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
                }
            }
        }
    }

    public java.net.URL toJavaURL() {
        try {
            return new java.net.URL(toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(host, port);
    }

    public String getServiceInterface() {
        return getParameter(Constants.INTERFACE_KEY, path);
    }

    public static String encode(String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String decode(String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Map<String, Number> getNumbers() {
        if (numbers == null) {
            numbers = new ConcurrentHashMap<String, Number>();
        }
        return numbers;
    }


}
