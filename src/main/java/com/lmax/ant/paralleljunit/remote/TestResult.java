/**
 * Copyright 2012-2013 LMAX Ltd.
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
