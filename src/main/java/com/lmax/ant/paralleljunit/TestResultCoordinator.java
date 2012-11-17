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

import com.lmax.ant.paralleljunit.remote.TestResult;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;


import static com.lmax.ant.paralleljunit.remote.TestResult.CRASHED;
import static com.lmax.ant.paralleljunit.remote.TestResult.ERROR;
import static com.lmax.ant.paralleljunit.remote.TestResult.FAILURE;
import static com.lmax.ant.paralleljunit.remote.TestResult.SUCCESS;

public class TestResultCoordinator
{
    private TestResult currentResult = SUCCESS;
    private boolean shouldContinue = true;
    private JUnitTest failedTest;

    public synchronized void notifyResult(final JUnitTest test, final TestResult result, final ParallelJUnitTaskConfig config)
    {
        if (result != SUCCESS)
        {
            handleResult(test, result, config);
        }
    }

    public synchronized boolean shouldContinue()
    {
        return shouldContinue;
    }

    public synchronized void onFinished(final ParallelJUnitTaskConfig config)
    {
        if (!shouldContinue)
        {
            throw new BuildException(createMessage(failedTest, currentResult), config.getProjectComponent().getLocation());
        }

        if (currentResult == ERROR && failedTest.getErrorProperty() != null)
        {
            config.getProjectComponent().getProject().setNewProperty(failedTest.getErrorProperty(), "true");
        }

        if (currentResult == FAILURE && failedTest.getFailureProperty() != null)
        {
            config.getProjectComponent().getProject().setNewProperty(failedTest.getFailureProperty(), "true");
        }
    }

    private String createMessage(final JUnitTest test, final TestResult result)
    {
        //TODO novakd handle timouts and crashes
        return test.getName() + " FAILED" + ((result == CRASHED) ? " (crashed)" : "");
    }

    private void handleResult(final JUnitTest test, final TestResult result, final ParallelJUnitTaskConfig config)
    {
        if (config.isLogFailedTests())
        {
            config.getProjectComponent().log(createMessage(test, result), Project.MSG_ERR);
        }

        // Just record the first errant test. Unless this test is a crash, in which case record that in preference. This is because the error message includes notification that the test crashed
        // and it would otherwise look odd to record and error and then later get a crash and then report "FAILED: ErroredTest (crash)"
        /*if (failedTest == null || result == TestResult.CRASHED)
        {
            failedTest = test;
        }

        if (result == TestResult.CRASHED)
        {
            crashed = true;
            shouldContinue = false;
        }
        else if (result == TestResult.ERROR)
        {
            errored = true;
            shouldContinue = shouldContinue & test.getHaltonerror();    //   <<<< THIS ISN'T QUITE RIGHT
        }
        else if (result == TestResult.FAILURE)
        {
            failed = true;
            shouldContinue = shouldContinue & test.getHaltonfailure(); //    <<<< DITTO
        }*/

        if (currentResult != CRASHED)
        {
            currentResult = result;
            failedTest = test;
            if (currentResult == CRASHED ||
                (test.getHaltonerror() && (currentResult == ERROR)) ||
                (test.getHaltonfailure() && (currentResult == FAILURE)))
            {
                shouldContinue = false;
            }
        }
    }
}
