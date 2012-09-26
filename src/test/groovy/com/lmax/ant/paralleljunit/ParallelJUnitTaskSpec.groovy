package com.lmax.ant.paralleljunit;


import com.lmax.ant.paralleljunit.remote.process.RemoteTestRunner
import org.apache.tools.ant.BuildException
import org.apache.tools.ant.Project
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest
import org.apache.tools.ant.types.Assertions
import org.apache.tools.ant.types.CommandlineJava
import org.apache.tools.ant.types.EnumeratedAttribute
import org.apache.tools.ant.types.Environment
import org.apache.tools.ant.types.Path
import org.apache.tools.ant.types.PropertySet
import spock.lang.Specification
import spock.lang.Subject

import static org.apache.tools.ant.taskdefs.optional.junit.JUnitTask.SummaryAttribute
import static org.apache.tools.ant.types.Commandline.Argument
import static org.apache.tools.ant.types.Environment.Variable

public class ParallelJUnitTaskSpec extends Specification {
    private CommandlineJava commandlineJava = Mock()
    private Environment environment = Mock()
    private WorkerCoordinator workerCoordinator = Mock()
    private BatchTestFactory batchTestFactory = Mock()
    private Project project = Mock()
    private ThreadsParser threadsParser = Mock()
    private List<DelegatingBatchTest> batchTests = []

    @Subject
    private ParallelJUnitTask task = new ParallelJUnitTask(commandlineJava, environment, workerCoordinator, batchTestFactory, threadsParser, batchTests)

    def setup() {
        task.project = project
    }

    def 'Appends runtime dependencies to command line on init'() {

        given:
        Path classpath = Mock()
        Path remoteRunnerPath = Mock()

        when:
        task.init()

        then:
        1 * commandlineJava.createClasspath(project) >> classpath
        1 * classpath.createPath() >> remoteRunnerPath
        1 * remoteRunnerPath.setLocation({ it.name ==~ /ant-junit-[\d\.]+.jar/ })
        1 * remoteRunnerPath.setLocation({ it.name ==~ /ant-launcher-[\d\.]+.jar/ })
        1 * remoteRunnerPath.setLocation({ it.name ==~ /ant-[\d\.]+.jar/ })
        1 * remoteRunnerPath.setLocation({ it.canonicalPath =~ /\/target\/classes$/ })
    }

    def 'Do not add command line argument when print summary is off'() {

        when:
        task.printSummary = EnumeratedAttribute.getInstance(SummaryAttribute, 'off')

        then:
        0 * commandlineJava._
    }

    def 'Add command line argument when print summary is on'() {

        given:
        Argument printSummaryAttribute = Mock()

        when:
        task.printSummary = EnumeratedAttribute.getInstance(SummaryAttribute, 'on')

        then:
        1 * commandlineJava.createArgument() >> printSummaryAttribute
        1 * printSummaryAttribute.setValue('formatter=org.apache.tools.ant.taskdefs.optional.junit.SummaryJUnitResultFormatter')
    }

    def 'Add command line argument when print summary is withOutAndErr'() {

        given:
        Argument printSummaryAttribute = Mock()

        when:
        task.printSummary = EnumeratedAttribute.getInstance(SummaryAttribute, 'withOutAndErr')

        then:
        1 * commandlineJava.createArgument() >> printSummaryAttribute
        1 * printSummaryAttribute.setValue('formatter=org.apache.tools.ant.taskdefs.optional.junit.OutErrSummaryJUnitResultFormatter')
    }

    def 'Sets max memory on command line'() {

        when:
        task.maxMemory = 'Lots'

        then:
        1 * commandlineJava.setMaxmemory('Lots')
    }

    def 'Sets the jvm on command line'() {

        when:
        task.jvm = 'DifferentJvm'

        then:
        1 * commandlineJava.setVm('DifferentJvm')
    }

    def 'Adds showoutput argument to command line'() {

        given:
        Argument argument = Mock()

        when:
        task.showOutput = false

        then:
        1 * commandlineJava.createArgument() >> argument
        1 * argument.setValue('showoutput=false')
    }

    def 'Sets cloneVm on command line'() {

        when:
        task.cloneVm = true

        then:
        1 * commandlineJava.setCloneVm(true)
    }

    def 'Adds logfailedtests argument to command line when logging failed tests requested'() {

        given:
        Argument argument = Mock()

        when:
        task.logFailedTests = true

        then:
        1 * commandlineJava.createArgument() >> argument
        1 * argument.setValue('logfailedtests=true')
    }

    def 'Calculates number of processes to use'() {

        when:
        task.threads = 'many'

        then:
        1 * threadsParser.parse('many') >> 2
        task.threads == 2
    }

    def 'Collects configured batch tests'() {

        given:
        DelegatingBatchTest batchTest1 = new DelegatingBatchTest(project)
        DelegatingBatchTest batchTest2 = new DelegatingBatchTest(project)

        when:
        task.createBatchTest()
        task.createBatchTest()

        then:
        2 * batchTestFactory.createBatchTest(project) >>> [batchTest1, batchTest2]
        batchTests == [batchTest1, batchTest2]
    }

    def 'Sets task default on newly created batch test'() {

        given:
        DelegatingBatchTest batchTest = Mock()
        batchTestFactory.createBatchTest(project) >> batchTest

        FormatterElement formatter1 = new FormatterElement()
        FormatterElement formatter2 = new FormatterElement()

        task.filterTrace = true
        task.haltOnError = true
        task.errorProperty = 'errorProperTea'
        task.haltOnFailure = true
        task.failureProperty = 'failureProperTea'
        task.addFormatter(formatter1)
        task.addFormatter(formatter2)

        when:
        task.createBatchTest()

        then:
        1 * batchTest.setFiltertrace(true)
        1 * batchTest.setHaltonerror(true)
        1 * batchTest.setErrorProperty('errorProperTea')
        1 * batchTest.setHaltonfailure(true)
        1 * batchTest.setFailureProperty('failureProperTea')
        1 * batchTest.addFormatter(formatter1)
        1 * batchTest.addFormatter(formatter2)
    }

    def 'Creates jvm args'() {

        given:
        Argument vmArgument = new Argument()
        commandlineJava.createVmArgument() >> vmArgument

        expect:
        task.createJvmArg() == vmArgument
    }

    def 'Collects system properties'() {

        given:
        Variable property = new Variable()

        when:
        task.addConfiguredSysProperty(property)

        then:
        1 * commandlineJava.addSysproperty(property)
    }

    def 'Collects system property sets'() {

        given:
        PropertySet propertySet = new PropertySet()

        when:
        task.addSysPropertySet(propertySet)

        then:
        1 * commandlineJava.addSyspropertyset(propertySet)
    }

    def 'Collects environment variables'() {

        given:
        Variable envVar = new Variable()

        when:
        task.addEnv(envVar)

        then:
        1 * environment.addVariable(envVar)
    }

    def 'Creates boot class path'() {

        given:
        Path newBootClassPath = new Path(project)
        Path baseBootClassPath = Mock()

        when:
        Path bootClassPath = task.createBootClassPath()

        then:
        1 * commandlineJava.createBootclasspath(project) >> baseBootClassPath
        1 * baseBootClassPath.createPath() >> newBootClassPath
        bootClassPath == newBootClassPath
    }

    def 'Creates class path'() {

        given:
        Path newClassPath = new Path(project)
        Path baseClassPath = Mock()

        when:
        Path classPath = task.createClasspath()

        then:
        1 * commandlineJava.createClasspath(project) >> baseClassPath
        1 * baseClassPath.createPath() >> newClassPath
        classPath == newClassPath
    }

    def 'Accepts assertions'() {

        given:
        Assertions assertions = new Assertions()
        commandlineJava.assertions >> null

        when:
        task.addAssertions(assertions)

        then:
        1 * commandlineJava.setAssertions(assertions)
    }

    def 'Accepts assertions only once'() {

        given:
        Assertions assertions = new Assertions()
        commandlineJava.assertions >> assertions

        when:
        task.addAssertions(assertions)

        then:
        0 * commandlineJava.setAssertions(_)
        thrown(BuildException)
    }

    def 'Populates test queue and delegates to worker coordinator on execute'() {

        given:
        DelegatingBatchTest batchTest = Mock()
        batchTests << batchTest
        List<JUnitTest> tests = [new JUnitTest('test1'), new JUnitTest('test2')]
        Enumeration<JUnitTest> testsEnumeration = new Vector<JUnitTest>(tests).elements()

        when:
        task.execute()

        then:
        1 * batchTest.elements() >> testsEnumeration
        task.getTestQueue() as List == tests

        then:
        1 * workerCoordinator.execute(task)
    }

    def 'Creates command'() {

        given:
        CommandlineJava cloned = Mock()
        Argument serverPortArgument = Mock()
        Argument workerIdArgument = Mock()
        cloned.createArgument() >>> [serverPortArgument, workerIdArgument]

        when:
        List<String> command = task.getCommand(RemoteTestRunner, 12, 6543)

        then:
        1 * commandlineJava.clone() >> cloned
        1 * cloned.setClassname(RemoteTestRunner.canonicalName)
        1 * serverPortArgument.setValue('serverPort=6543')
        1 * workerIdArgument.setValue('workerId=12')
        1 * cloned.commandline >> ['bull', 'excrement']
        command == ['bull', 'excrement']
    }

    def 'Adds logtestlistenerevents argument if magic ant.junit.enabletestlistenerevents property is set to true'() {

        given:
        CommandlineJava cloned = Mock()
        commandlineJava.clone() >> cloned
        Argument logTestListenerEventsArgument = Mock()
        cloned.createArgument() >>> [Mock(Argument), Mock(Argument), logTestListenerEventsArgument]
        cloned.commandline >> ['bull', 'excrement']

        when:
        task.getCommand(RemoteTestRunner, 12, 6543)

        then:
        1 * project.getProperty('ant.junit.enabletestlistenerevents') >> 'true'
        1 * logTestListenerEventsArgument.setValue('logtestlistenerevents=true')
    }

    def 'Adds logtestlistenerevents argument if enableTestListenerEvents is set'() {

        given:
        CommandlineJava cloned = Mock()
        commandlineJava.clone() >> cloned
        Argument logTestListenerEventsArgument = Mock()
        cloned.createArgument() >>> [Mock(Argument), Mock(Argument), logTestListenerEventsArgument]
        cloned.commandline >> ['bull', 'excrement']
        task.enableTestListenerEvents = true

        when:
        task.getCommand(RemoteTestRunner, 12, 6543)

        then:
        1 * logTestListenerEventsArgument.setValue('logtestlistenerevents=true')
    }

    def 'Returns empty map as environment if no environment variables set'() {

        expect:
        task.environment.isEmpty()
    }

    def 'Returns map containing configured environment variables'() {

        given:
        environment.variables >> ['cat=tom', 'cheese', 'mouse=jerry']

        expect:
        task.environment == [cat: 'tom', mouse: 'jerry']
    }
}
