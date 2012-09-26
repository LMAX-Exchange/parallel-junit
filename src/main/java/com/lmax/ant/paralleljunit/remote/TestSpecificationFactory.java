package com.lmax.ant.paralleljunit.remote;

import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;


public class TestSpecificationFactory
{
    public TestSpecification create(final JUnitTest test)
    {
        return new TestSpecification(test);
    }
}
