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

package com.lmax.ant.paralleljunit;

import java.io.File;
import java.util.Enumeration;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.junit.BatchTest;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ResourceCollection;

public class DelegatingBatchTest
{
    private final BatchTest delegate;

    public DelegatingBatchTest(final BatchTest delegate)
    {
        this.delegate = delegate;
    }

    public DelegatingBatchTest(final Project project)
    {
        this(new BatchTest(project));
    }

    public void addFileSet(final FileSet fs)
    {
        delegate.addFileSet(fs);
    }

    public void add(final ResourceCollection rc)
    {
        delegate.add(rc);
    }

    @SuppressWarnings("unchecked")
    public Enumeration<JUnitTest> elements()
    {
        return delegate.elements();
    }

    public static String javaToClass(final String filename)
    {
        return BatchTest.javaToClass(filename);
    }

    public void setFiltertrace(final boolean value)
    {
        delegate.setFiltertrace(value);
    }

    public boolean getFiltertrace()
    {
        return delegate.getFiltertrace();
    }

    public void setFork(final boolean value)
    {
        delegate.setFork(value);
    }

    public boolean getFork()
    {
        return delegate.getFork();
    }

    public void setHaltonerror(final boolean value)
    {
        delegate.setHaltonerror(value);
    }

    public void setHaltonfailure(final boolean value)
    {
        delegate.setHaltonfailure(value);
    }

    public boolean getHaltonerror()
    {
        return delegate.getHaltonerror();
    }

    public boolean getHaltonfailure()
    {
        return delegate.getHaltonfailure();
    }

    public void setIf(final Object ifCondition)
    {
        delegate.setIf(ifCondition);
    }

    public void setIf(final String propertyName)
    {
        delegate.setIf(propertyName);
    }

    public Object getIfCondition()
    {
        return delegate.getIfCondition();
    }

    public void setUnless(final Object unlessCondition)
    {
        delegate.setUnless(unlessCondition);
    }

    public void setUnless(final String propertyName)
    {
        delegate.setUnless(propertyName);
    }

    public Object getUnlessCondition()
    {
        return delegate.getUnlessCondition();
    }

    public void addFormatter(final FormatterElement elem)
    {
        delegate.addFormatter(elem);
    }

    public void setTodir(final File destDir)
    {
        delegate.setTodir(destDir);
    }

    public String getTodir()
    {
        return delegate.getTodir();
    }

    public String getFailureProperty()
    {
        return delegate.getFailureProperty();
    }

    public void setFailureProperty(final String failureProperty)
    {
        delegate.setFailureProperty(failureProperty);
    }

    public String getErrorProperty()
    {
        return delegate.getErrorProperty();
    }

    public void setErrorProperty(final String errorProperty)
    {
        delegate.setErrorProperty(errorProperty);
    }
}
