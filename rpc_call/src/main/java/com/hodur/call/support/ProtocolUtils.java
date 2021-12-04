package com.hodur.call.support;


import com.hodur.common.Constants;
import com.hodur.common.URL;
import io.netty.util.Constant;

/**
 * @author Hodur
 * @className ProtocolUtils.java
 * @description
 * @date 2021年12月04日 9:48
 */
public class ProtocolUtils {
    private ProtocolUtils() {}

    public static String serviceKey(URL url) {
        return serviceKey(url.getPort(), url.getPath(), url.getParameter(Constants.VERSION_KEY), url.getParameter(Constants.GROUP_KEY));
    }
    /**
     * @describe
     * @author Hodur
     * @date 2021/12/4 10:04
     * @param port
     * @param serviceName
     * @param serviceVersion
     * @param serviceGroup
     * @return java.lang.String
     */
    public static String serviceKey(int port, String serviceName, String serviceVersion, String serviceGroup) {
        StringBuilder buf = new StringBuilder();
        if (serviceGroup != null && serviceGroup.length() > 0) {
            buf.append(serviceGroup);
            buf.append("/");
        }
        buf.append(serviceName);
        if (serviceVersion != null && serviceVersion.length() > 0 && !"0.0.0".equals(serviceVersion)) {
            buf.append(":");
            buf.append(serviceVersion);
        }
        buf.append(":");
        buf.append(port);
        return buf.toString();
    }
}
