package com.lmax.ant.paralleljunit.remote.process;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.net.SocketFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExitException;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner;
import org.apache.tools.ant.util.optional.NoExitSecurityManager;

import com.lmax.ant.paralleljunit.remote.TestResult;
import com.lmax.ant.paralleljunit.remote.TestSpecification;
import com.lmax.ant.paralleljunit.util.net.SocketConnection;
import com.lmax.ant.paralleljunit.util.net.SocketConnectionFactory;

public class RemoteTestRunner
{
    private static final SecurityManager NO_EXIT_SECURITY_MANAGER = new NoExitSecurityManager();

    private final RemoteTestRunnerParams paramsRemote;
    private final SocketConnectionFactory connectionFactory;

    public RemoteTestRunner(final RemoteTestRunnerParams paramsRemote, final SocketConnectionFactory connectionFactory)
    {
        this.paramsRemote = paramsRemote;
        this.connectionFactory = connectionFactory;
    }

    public static void main(final String... args)
    {
        final ArgsParser parser = new ArgsParser();
        final RemoteTestRunnerParams paramsRemote = parser.parseMainArgs(args);

        final SocketConnectionFactory connectionFactory = new SocketConnectionFactory(SocketFactory.getDefault());

        final RemoteTestRunner testRunner = new RemoteTestRunner(paramsRemote, connectionFactory);
        testRunner.executeTests();
    }

    private void executeTests()
    {
        try
        {
            final SocketConnection socketConnection = connectionFactory.createSocketConnection(paramsRemote.getServerPort());

            final Collection<?> formatters = getTestRunnerFormatters();

            RemoteTestRunnerCommand command = null;
            while ((command = socketConnection.readObject()) != null)
            {
                switch (command)
                {
                    case EXIT:
                        socketConnection.close();
                        System.exit(0);
                        break;
                    case RUN_TEST:
                        formatters.clear();
                        socketConnection.writeObject(runTest(socketConnection.<TestSpecification>readObject(), paramsRemote));
                        break;
                    default:
                        throw new RuntimeException("All hell broke loose");
                }
            }
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        catch (final ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private Collection<?> getTestRunnerFormatters()
    {
        try
        {
            final Field fromCmdLine = JUnitTestRunner.class.getDeclaredField("fromCmdLine");
            fromCmdLine.setAccessible(true);
            return (Collection<?>)fromCmdLine.get(null);
        }
        catch (final NoSuchFieldException e)
        {
            throw new BuildException("Error Tinkering with formatters.", e);
        }
        catch (final IllegalAccessException e)
        {
            throw new BuildException("Error Tinkering with formatters.", e);
        }
    }

    private TestResult runTest(final TestSpecification testSpec, final RemoteTestRunnerParams paramsRemote)
    {
        final List<String> jUnitRunnerArgs = new LinkedList<String>();
        jUnitRunnerArgs.addAll(testSpec.toArgs());
        jUnitRunnerArgs.addAll(paramsRemote.getTestRunnerArguments());

        final SecurityManager standardSecurityManager = System.getSecurityManager();
        System.setSecurityManager(NO_EXIT_SECURITY_MANAGER);
        try
        {
            JUnitTestRunner.main(jUnitRunnerArgs.toArray(new String[jUnitRunnerArgs.size()]));
            return TestResult.SUCCESS;
        }
        catch (final IOException e)
        {
            throw new BuildException("Error invoking test runner ", e);
        }
        catch (final ExitException e)
        {
            return TestResult.fromExitCode(e.getStatus());
        }
        finally
        {
            System.setSecurityManager(standardSecurityManager);
        }
    }

}
