package com.lmax.ant.paralleljunit.util.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Callable;

public class ConnectionEstablisher implements Callable<SocketConnection>
{
    private final ServerSocket serverSocket;

    public ConnectionEstablisher(final ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    public SocketConnection call() throws Exception
    {
        try
        {
            return new SocketConnection(serverSocket.accept());
        }
        finally
        {
            try
            {
                serverSocket.close();
            }
            catch (IOException e)
            {
                // close silently
            }
        }
    }
}
