package com.lmax.ant.paralleljunit.util.io;

import java.io.InputStream;

public class EOFAwareInputStreamFactory
{
    public EOFAwareInputStream create(final InputStream delegate)
    {
        return new EOFAwareInputStream(delegate);
    }
}
