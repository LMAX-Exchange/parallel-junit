package com.lmax.ant.paralleljunit.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;

public class ExecuteStreamHandlerFactory
{
    private final PumpStreamHandlerFactory pumpStreamHandlerFactory;

    public ExecuteStreamHandlerFactory(final PumpStreamHandlerFactory pumpStreamHandlerFactory)
    {
        this.pumpStreamHandlerFactory = pumpStreamHandlerFactory;
    }

    public ExecuteStreamHandler create(final InputStream processOutputStream, final InputStream processErrorStream, final OutputStream processInputStream)
    {
        final ExecuteStreamHandler streamHandler = pumpStreamHandlerFactory.create();
        try
        {
            streamHandler.setProcessOutputStream(processOutputStream);
            streamHandler.setProcessErrorStream(processErrorStream);
            streamHandler.setProcessInputStream(processInputStream);
            streamHandler.start();
        }
        catch (IOException e)
        {
            // The ExecuteStreamHandler interface declares IOException on all the above methods, but the PumpStreamHandler implementation never throws them
            throw new BuildException(e);
        }
        return streamHandler;
    }
}
