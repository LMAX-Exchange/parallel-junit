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
}
