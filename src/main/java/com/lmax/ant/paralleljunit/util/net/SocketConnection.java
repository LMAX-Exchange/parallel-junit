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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketConnection
{
    private final Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public SocketConnection(final Socket socket)
    {
        this.socket = socket;
    }

    public void close()
    {
        try
        {
            socket.close();
        }
        catch (final IOException e)
        {
            // close silently
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T readObject() throws IOException, ClassNotFoundException
    {
        return (T)getObjectInputStream().readObject();
    }

    public void writeObject(final Object object) throws IOException
    {
        final ObjectOutputStream outputStream = getObjectOutputStream();
        outputStream.writeObject(object);
        outputStream.flush();
    }

    private ObjectInputStream getObjectInputStream() throws IOException
    {
        if (objectInputStream == null)
        {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        }
        return objectInputStream;
    }

    private ObjectOutputStream getObjectOutputStream() throws IOException
    {
        if (objectOutputStream == null)
        {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        }
        return objectOutputStream;
    }
}
