/*
 * Copyright 2012 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
