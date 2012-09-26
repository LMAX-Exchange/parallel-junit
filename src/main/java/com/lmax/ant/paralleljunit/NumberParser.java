package com.lmax.ant.paralleljunit;

import org.apache.tools.ant.BuildException;

class NumberParser
{
    public int parse(final String number)
    {
        try
        {
            return Integer.parseInt(number);
        }
        catch (NumberFormatException e)
        {
            throw new BuildException("Not a parsable integer " + number, e);
        }
    }
}
