package com.lmax.ant.paralleljunit.util.net;

import java.net.ServerSocket;

public class ConnectionEstablisherFactory
{
    public ConnectionEstablisher create(final ServerSocket serverSocket)
    {
        return new ConnectionEstablisher(serverSocket);
    }
}
