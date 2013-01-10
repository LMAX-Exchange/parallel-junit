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
