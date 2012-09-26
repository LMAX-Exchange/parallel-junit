package com.lmax.ant.paralleljunit;


public class ThreadsParser
{
    private final PercentileParser percentileParser;
    private final AdditiveParser additiveParser;
    private final NumberParser numberParser;

    public ThreadsParser(final PercentileParser percentileParser, final AdditiveParser additiveParser, final NumberParser numberParser)
    {
        this.percentileParser = percentileParser;
        this.additiveParser = additiveParser;
        this.numberParser = numberParser;
    }

    public int parse(final String threadsParam)
    {
        if (threadsParam.endsWith("%"))
        {
            return percentileParser.parse(threadsParam);
        }
        if (threadsParam.startsWith("-"))
        {
            return additiveParser.parse(threadsParam);
        }
        return numberParser.parse(threadsParam);
    }
}
