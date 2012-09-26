package com.lmax.ant.paralleljunit.remote.process;

import java.util.List;

class RemoteTestRunnerParams
{
    private final int workerId;
    private final int serverPort;

    private final List<String> testRunnerArguments;

    RemoteTestRunnerParams(final int workerId, final int serverPort, final List<String> testRunnerArguments)
    {
        this.workerId = workerId;
        this.serverPort = serverPort;
        this.testRunnerArguments = testRunnerArguments;
    }

    public int getServerPort()
    {
        return serverPort;
    }

    public int getWorkerId()
    {
        return workerId;
    }

    public List<String> getTestRunnerArguments()
    {
        return testRunnerArguments;
    }
}
