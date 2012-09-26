package com.lmax.ant.paralleljunit.util.io;

import java.io.OutputStream;

import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

public class PumpStreamHandlerFactory
{
    private final OutputStream outputStream;
    private final OutputStream errorStream;

    public PumpStreamHandlerFactory(final OutputStream outputStream, final OutputStream errorStream)
    {
        this.outputStream = outputStream;
        this.errorStream = errorStream;
    }

    public ExecuteStreamHandler create()
    {
        return new PumpStreamHandler(outputStream, errorStream);
    }

}
