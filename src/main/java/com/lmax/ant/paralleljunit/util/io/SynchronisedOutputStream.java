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
