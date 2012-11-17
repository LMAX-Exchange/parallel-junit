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
