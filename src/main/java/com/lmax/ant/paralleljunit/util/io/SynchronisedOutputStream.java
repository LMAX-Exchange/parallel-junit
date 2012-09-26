package com.lmax.ant.paralleljunit.util.io;

import java.io.IOException;
import java.io.OutputStream;

public final class SynchronisedOutputStream extends OutputStream
{
    private final OutputStream stream;

    public SynchronisedOutputStream(final OutputStream stream)
    {
        this.stream = stream;
    }

    @Override
    public synchronized void write(final int b) throws IOException
    {
        stream.write(b);
    }

    @Override
    public synchronized void write(final byte[] b) throws IOException
    {
        stream.write(b);
    }

    @Override
    public synchronized void write(final byte[] b, final int off, final int len) throws IOException
    {
        stream.write(b, off, len);
    }

    @Override
    public synchronized void flush() throws IOException
    {
        stream.flush();
    }

    @Override
    public synchronized void close() throws IOException
    {
        stream.close();
    }
}
