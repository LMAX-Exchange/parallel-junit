package com.lmax.ant.paralleljunit.util.process;

public class ProcessFinishedWaiter implements Runnable
{
    private final Process process;

    public ProcessFinishedWaiter(final Process process)
    {
        this.process = process;
    }

    public void run()
    {
        try
        {
            process.waitFor();
        }
        catch (InterruptedException e)
        {
            // om nom nom
        }
    }
}
