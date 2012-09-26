package com.lmax.ant.paralleljunit.util.process;

import java.util.List;

public class ProcessBuilderFactory
{
    public DelegatingProcessBuilder createProcessBuilder(final List<String> command)
    {
        return new DelegatingProcessBuilder(new ProcessBuilder(command));
    }
}
