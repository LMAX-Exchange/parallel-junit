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
package com.lmax.ant.paralleljunit.remote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;

public class TestSpecification implements Serializable
{
    private static final long serialVersionUID = -3153106877519338356L;

    private final String testName;
    private final boolean filterTrace;
    private final boolean haltOnError;
    private final boolean haltOnFailure;
    private final String outFile;
    private final String toDir;

    private final List<FormatterSpecification> formatters;


    /**
     * Create a test spec from a JUnitTest
     */
    public TestSpecification(final JUnitTest test)
    {
        testName = test.getName();
        filterTrace = test.getFiltertrace();
        haltOnError = test.getHaltonerror();
        haltOnFailure = test.getHaltonfailure();
        toDir = test.getTodir() != null ? test.getTodir() : "";
        outFile = test.getOutfile() != null ? test.getOutfile() : "TEST-" + test.getName();

        formatters = new ArrayList<FormatterSpecification>(test.getFormatters().length);
        for (final FormatterElement formatterElement : test.getFormatters())
        {
            formatters.add(new FormatterSpecification(formatterElement));
        }
    }

    public List<String> toArgs()
    {
        final List<String> args = new ArrayList<String>(4 + formatters.size());
        args.add(testName);
        args.add("filtertrace=" + filterTrace);
        args.add("haltOnError=" + haltOnError);
        args.add("haltOnFailure=" + haltOnFailure);
        for (final FormatterSpecification formatter : formatters)
        {
            args.add("formatter=" + formatter.className + "," + toDir + (toDir.endsWith("/") ? "" : "/") + outFile + formatter.extension);
        }
        return args;
    }

    private static class FormatterSpecification implements Serializable
    {
        private static final long serialVersionUID = 3059499247578354219L;

        private final String className;
        private final String extension;

        private FormatterSpecification(final FormatterElement element)
        {
            className = element.getClassname();
            extension = element.getExtension();
        }
    }
}
