package com.lmax.ant.paralleljunit.remote.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import javax.net.ServerSocketFactory;

import com.lmax.ant.paralleljunit.ParallelJUnitTaskConfig;
import com.lmax.ant.paralleljunit.remote.TestSpecificationFactory;
import com.lmax.ant.paralleljunit.util.net.ConnectionEstablisherFactory;
import com.lmax.ant.paralleljunit.util.net.SocketConnection;
import com.lmax.ant.paralleljunit.util.process.ManagedProcess;
import com.lmax.ant.paralleljunit.util.process.ManagedProcessFactory;

import org.apache.tools.ant.BuildException;


import static java.util.concurrent.TimeUnit.SECONDS;

public class RemoteTestRunnerControllerFactory
{
    private static final int ANY_FREE_PORT = 0;

    private final ManagedProcessFactory managedProcessFactory;
    private final ExecutorService executorService;
    private final ServerSocketFactory serverSocketFactory;
    private final ConnectionEstablisherFactory connectionEstablisherFactory;
    private final TestSpecificationFactory testSpecificationFactory;

    public RemoteTestRunnerControllerFactory(final ManagedProcessFactory managedProcessFactory, final ExecutorService executorService, final ServerSocketFactory serverSocketFactory,
                                             final ConnectionEstablisherFactory connectionEstablisherFactory, final TestSpecificationFactory testSpecificationFactory)
    {
        this.managedProcessFactory = managedProcessFactory;
        this.executorService = executorService;
        this.serverSocketFactory = serverSocketFactory;
        this.connectionEstablisherFactory = connectionEstablisherFactory;
        this.testSpecificationFactory = testSpecificationFactory;
    }

    public RemoteTestRunnerController create(final int workerId, final ParallelJUnitTaskConfig config)
    {
        final ServerSocket serverSocket = createServerSocket();
        final Future<SocketConnection> connectionFuture = executorService.submit(connectionEstablisherFactory.create(serverSocket));
        final ManagedProcess jvmProcess = managedProcessFactory.create(workerId, config, serverSocket.getLocalPort());
        final SocketConnection socketConnection = waitForSocketConnection(connectionFuture);
        return new RemoteTestRunnerController(jvmProcess, socketConnection, testSpecificationFactory);
    }

    private ServerSocket createServerSocket()
    {
        try
        {
            return serverSocketFactory.createServerSocket(ANY_FREE_PORT);
        }
        catch (final IOException e)
        {
            throw new BuildException("Could not open server socket.", e);
        }
    }

    private SocketConnection waitForSocketConnection(final Future<SocketConnection> connectionFuture)
    {
        try
        {
            return connectionFuture.get(10, SECONDS);
        }
        catch (final InterruptedException e)
        {
            throw new BuildException("Interrupted while waiting to accept connection.", e);
        }
        catch (final ExecutionException e)
        {
            throw new BuildException("Accepting connection caused an exception.", e);
        }
        catch (final TimeoutException e)
        {
            throw new BuildException("Timed out while waiting to accept connection.", e);
        }
    }
}
