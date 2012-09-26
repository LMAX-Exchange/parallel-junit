package com.lmax.ant.paralleljunit

import spock.lang.Specification
import spock.lang.Subject


class WorkerFactorySpec extends Specification
{

    @Subject
    WorkerFactory workerFactory = new WorkerFactory(null, null);

    def 'creates workers'(){

        given:
        ParallelJUnitTaskConfig config = Mock()

        when:
        Worker worker = workerFactory.createWorker(12, config)

        then:
        1 * config.getTestQueue()
        worker != null
    }
}