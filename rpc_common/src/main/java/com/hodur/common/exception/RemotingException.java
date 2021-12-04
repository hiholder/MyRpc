package com.hodur.common.exception;

import java.net.InetSocketAddress;
import java.nio.channels.Channel;

/**
 * @author Hodur
 * @className RemotingException.java
 * @description
 * @date 2021年12月04日 22:48
 */
public class RemotingException extends Exception {
    private static final long serialVersionUID = -3160452149606778709L;

    private InetSocketAddress localAddress;

    private InetSocketAddress remoteAddress;



    public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message,
                             Throwable cause) {
        super(message, cause);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }
}
