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
package com.lmax.ant.paralleljunit.util.process;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DelegatingProcessBuilder
{

    private final ProcessBuilder delegate;

    public DelegatingProcessBuilder(final ProcessBuilder delegate)
    {
        this.delegate = delegate;
    }

    public DelegatingProcessBuilder command(final List<String> command)
    {
        delegate.command(command);
        return this;
    }

    public DelegatingProcessBuilder command(final String... command)
    {
        delegate.command(command);
        return this;
    }

    public List<String> command()
    {
        return delegate.command();
    }

    public Map<String, String> environment()
    {
        return delegate.environment();
    }

    public File directory()
    {
        return delegate.directory();
    }

    public DelegatingProcessBuilder directory(final File directory)
    {
        delegate.directory(directory);
        return this;
    }

    public boolean redirectErrorStream()
    {
        return delegate.redirectErrorStream();
    }

    public DelegatingProcessBuilder redirectErrorStream(final boolean redirectErrorStream)
    {
        delegate.redirectErrorStream(redirectErrorStream);
        return this;
    }

    public Process start() throws IOException
    {
        return delegate.start();
    }
}
