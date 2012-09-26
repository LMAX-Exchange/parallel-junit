package com.lmax.ant.paralleljunit.util.process

import com.lmax.ant.paralleljunit.util.io.EOFAwareInputStream
import org.apache.tools.ant.BuildException
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler
import org.apache.tools.ant.taskdefs.ExecuteWatchdog
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeoutException

import static java.util.concurrent.TimeUnit.SECONDS

class ManagedProcessSpec extends Specification {

    private Process process = Mock()
    private ProcessDestroyer processDestroyer = Mock()
    private ExecuteStreamHandler streamHandler = Mock()
    private ExecuteWatchdog watchdog = Mock()
    private Collection<EOFAwareInputStream> streams = []
    private ExecutorService executorService = Mock()

    @Subject
    private ManagedProcess managedProcess = new ManagedProcess(process, processDestroyer, streamHandler, watchdog, streams, executorService)

    def 'Waits 2 seconds for process to finish, stops the watchdog and removes process from destroyer on close'() {

        given:
        Future waiterFuture = Mock()

        when:
        managedProcess.close()

        then:
        1 * executorService.submit(_ as ProcessFinishedWaiter) >> waiterFuture
        1 * waiterFuture.get(2, SECONDS)

        then:
        1 * watchdog.stop()
        1 * processDestroyer.remove(process)
    }

    @Unroll('Destroys process when waiting for it to finish throws #exception')
    def 'Destroys process when waiting for it to finish is interrupted or times out'() {

        given:
        Future waiterFuture = Mock()
        waiterFuture.get(_, _) >> { throw exception }
        executorService.submit(_) >> waiterFuture

        when:
        managedProcess.close()

        then:
        1 * process.destroy()

        then:
        1 * watchdog.stop()
        1 * processDestroyer.remove(process)

        where:
        exception << [new InterruptedException(), new TimeoutException()]
    }

    def 'Throws BuildException when waiting for process to finish throws java.util.concurrent.ExecutionException'() {
        given:
        Future waiterFuture = Mock()
        waiterFuture.get(_, _) >> { throw new ExecutionException(new Exception()) }
        executorService.submit(_) >> waiterFuture

        when:
        managedProcess.close()

        then:
        1 * process.destroy()
        thrown(BuildException)
    }

    def 'Waits 2 seconds for each stream to reach EOF and stops stream handler on close'() {

        given:
        EOFAwareInputStream stream1 = Mock()
        EOFAwareInputStream stream2 = Mock()
        streams << stream1 << stream2

        executorService.submit(_) >> Mock(Future)

        when:
        managedProcess.close()

        then:
        1 * stream1.waitFor(2, SECONDS)
        1 * stream2.waitFor(2, SECONDS)
        1 * streamHandler.stop()
    }

    def 'Suppresses InterruptedException thrown when waiting for process stream is interrupted'() {

        given:
        EOFAwareInputStream stream1 = Mock()
        EOFAwareInputStream stream2 = Mock()
        streams << stream1 << stream2

        executorService.submit(_) >> Mock(Future)

        when:
        managedProcess.close()

        then:
        1 * stream1.waitFor(_, _) >> { throw new InterruptedException() }

        then:
        1 * stream2.waitFor(_, _)
        1 * streamHandler.stop()
    }
}
