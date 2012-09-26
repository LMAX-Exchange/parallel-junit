package com.lmax.ant.paralleljunit;

class AdditiveParser
{
    private final NumberParser numberParser;
    private final int defaultThreads;

    public AdditiveParser(final NumberParser numberParser, final int defaultThreads)
    {
        this.numberParser = numberParser;
        this.defaultThreads = defaultThreads;
    }

    public int parse(final String threadsParam)
    {
        return defaultThreads + numberParser.parse(threadsParam);
    }
}
