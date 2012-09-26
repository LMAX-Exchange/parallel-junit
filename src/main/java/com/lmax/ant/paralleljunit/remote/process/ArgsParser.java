package com.lmax.ant.paralleljunit.remote.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import static java.util.Arrays.asList;

public class ArgsParser
{
    private static final Collection<String> BLACK_LIST = asList("filtertrace=", "haltOnError=", "haltOnFailure=");
    private static final String WORKER_ID = "workerId=";
    private static final String SERVER_PORT = "serverPort=";

    public RemoteTestRunnerParams parseMainArgs(final String[] args)
    {
        int workerId = -1;
        int serverPort = -1;
        final List<String> testRunnerArguments = new ArrayList<String>(args.length);

        for (final String arg : args)
        {
            if (onBlackList(arg))
            {
                continue;
            }

            if (arg.startsWith(WORKER_ID))
            {
                workerId = parseIntValue(WORKER_ID, arg);
            }
            else if (arg.startsWith(SERVER_PORT))
            {
                serverPort = parseIntValue(SERVER_PORT, arg);
            }
            else
            {
                testRunnerArguments.add(arg);
            }
        }

        return new RemoteTestRunnerParams(workerId, serverPort, testRunnerArguments);
    }

    private static int parseIntValue(final String name, final String arg)
    {
        final String value = arg.substring(name.length());
        return Integer.parseInt(value);
    }

    private static boolean onBlackList(final String arg)
    {
        for (final String blackListItem : BLACK_LIST)
        {
            if (arg.startsWith(blackListItem))
            {
                return true;
            }
        }
        return false;
    }
}
