package com.lmax.ant.paralleljunit.remote;

import static org.apache.tools.ant.taskdefs.optional.junit.JUnitTaskMirror.JUnitTestRunnerMirror;

public enum TestResult
{
    SUCCESS(JUnitTestRunnerMirror.SUCCESS),
    FAILURE(JUnitTestRunnerMirror.FAILURES),
    ERROR(JUnitTestRunnerMirror.ERRORS),

    CRASHED(Const.NO_CODE);


    private final int exitCode;

    TestResult(final int exitCode)
    {
        this.exitCode = exitCode;
    }

    public static TestResult fromExitCode(final int exitCode)
    {
        for (final TestResult result : values())
        {
            if (exitCode == result.exitCode)
            {
                return result;
            }
        }

        return TestResult.CRASHED;
    }

    private static class Const
    {
        private static final int NO_CODE = Integer.MIN_VALUE;
    }
}
