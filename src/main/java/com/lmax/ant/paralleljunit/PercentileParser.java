package com.lmax.ant.paralleljunit;

import org.apache.tools.ant.BuildException;

class PercentileParser
{
    private NumberParser numberParser;
    private final int availableProcessors;

    public PercentileParser(final NumberParser numberParser, final int availableProcessors)
    {
        this.numberParser = numberParser;
        this.availableProcessors = availableProcessors;
    }

    public int parse(final String percentileThreadsParam)
    {
        final int percent = numberParser.parse(percentileThreadsParam.replace("%", ""));
        if (percent < 0)
        {
            throw new BuildException("Not a valid thread count [" + percentileThreadsParam + "]");
        }
        return (int) (0.01 * percent * availableProcessors);
    }

}
