package com.lmax.ant.paralleljunit;

import org.apache.tools.ant.Project;

public class BatchTestFactory
{
    public DelegatingBatchTest createBatchTest(final Project project)
    {
        return new DelegatingBatchTest(project);
    }
}
