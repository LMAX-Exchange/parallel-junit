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
package com.lmax.ant.paralleljunit;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;

public interface ParallelJUnitTaskConfig
{
    long NO_TIMEOUT = -1;

    Queue<JUnitTest> getTestQueue();

    List<String> getCommand(Class<?> mainClass, int workerId, int serverPort);

    File getDirectory();

    boolean isNewEnvironment();

    Map<String, String> getEnvironment();

    long getTimeout();

    boolean isLogFailedTests();

    ProjectComponent getProjectComponent();

    int getThreads();

    boolean isDirPerWorker();

    String getWorkerDirPrefix();
}
