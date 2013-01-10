/**
 * Copyright 2012-2013 LMAX Ltd.
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
