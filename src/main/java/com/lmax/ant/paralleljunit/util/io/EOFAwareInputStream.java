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
package com.lmax.ant.paralleljunit.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EOFAwareInputStream extends InputStream
{
    private final InputStream delegate;
    private final CountDownLatch endOfStream = new CountDownLatch(1);

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
        catch (final IOException e)
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
        catch (final IOException e)
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
        catch (final IOException e)
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
    public synchronized void mark(final int readlimit)
    {
        delegate.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException
    {
        delegate.reset();
    }

    @Override
    public boolean markSupported()
    {
        return delegate.markSupported();
    }

    private int checkEOF(final int bytes)
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
