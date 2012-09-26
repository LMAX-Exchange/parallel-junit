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
