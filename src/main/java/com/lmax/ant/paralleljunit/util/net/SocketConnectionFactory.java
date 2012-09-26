package com.lmax.ant.paralleljunit.util.net;


import java.io.IOException;
import java.net.InetAddress;

import javax.net.SocketFactory;

public class SocketConnectionFactory
{
    private final SocketFactory socketFactory;

    public SocketConnectionFactory(final SocketFactory socketFactory)
    {
        this.socketFactory = socketFactory;
    }

    public SocketConnection createSocketConnection(final int port) throws IOException
    {
        return new SocketConnection(socketFactory.createSocket(InetAddress.getLocalHost(), port));
    }
}
