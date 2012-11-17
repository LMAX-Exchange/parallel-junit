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

package com.lmax.ant.paralleljunit.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;

public class ExecuteStreamHandlerFactory
{
    private final PumpStreamHandlerFactory pumpStreamHandlerFactory;

    public ExecuteStreamHandlerFactory(final PumpStreamHandlerFactory pumpStreamHandlerFactory)
    {
        this.pumpStreamHandlerFactory = pumpStreamHandlerFactory;
    }

    public ExecuteStreamHandler create(final InputStream processOutputStream, final InputStream processErrorStream, final OutputStream processInputStream)
    {
        final ExecuteStreamHandler streamHandler = pumpStreamHandlerFactory.create();
        try
        {
            streamHandler.setProcessOutputStream(processOutputStream);
            streamHandler.setProcessErrorStream(processErrorStream);
            streamHandler.setProcessInputStream(processInputStream);
            streamHandler.start();
        }
        catch (IOException e)
        {
            // The ExecuteStreamHandler interface declares IOException on all the above methods, but the PumpStreamHandler implementation never throws them
            throw new BuildException(e);
        }
        return streamHandler;
    }
}
