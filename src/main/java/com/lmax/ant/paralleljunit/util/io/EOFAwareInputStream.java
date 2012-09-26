package com.lmax.ant.paralleljunit.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EOFAwareInputStream extends InputStream
{
    private final InputStream delegate;
    private CountDownLatch endOfStream = new CountDownLatch(1);

    public EOFAwareInputStream(final InputStream delegate)
    {
        this.delegate = delegate;
    }

    public boolean waitFor(final int timeout, final TimeUnit timeUnit) throws InterruptedException
    {
        return endOfStream.await(timeout, timeUnit);
    }

    @Override
    public int read() throws IOException
    {
        try
        {
            return checkEOF(delegate.read());
        }
        catch (IOException e)
        {
            endOfStream();
            throw e;
        }
    }

    @Override
    public int read(final byte[] b) throws IOException
    {
        try
        {
            return checkEOF(delegate.read(b));
        }
        catch (IOException e)
        {
            endOfStream();
            throw e;
        }
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException
    {
        try
        {
            return checkEOF(delegate.read(b, off, len));
        }
        catch (IOException e)
        {
            endOfStream();
            throw e;
        }
    }

    @Override
    public long skip(final long n) throws IOException
    {
        return delegate.skip(n);
    }

    @Override
    public int available() throws IOException
    {
        // This is a horrible subversion of Ant's StreamPumper.useAvailable. We effectively tell it there's always data available, so it calls read. It's quite happy if read returns 0 bytes. So this
        // doesn't break it. But in the mean time, our read method is invoked so we can see when EOF happens.
        return 1;
    }

    @Override
    public void close() throws IOException
    {
        delegate.close();
    }

    @Override
    public void mark(final int readlimit)
    {
        delegate.mark(readlimit);
    }

    @Override
    public void reset() throws IOException
    {
        delegate.reset();
    }

    @Override
    public boolean markSupported()
    {
        return delegate.markSupported();
    }

    private int checkEOF(int bytes)
    {
        if (bytes < 0)
        {
            endOfStream();
        }
        return bytes;
    }

    private void endOfStream()
    {
        endOfStream.countDown();
    }
}
