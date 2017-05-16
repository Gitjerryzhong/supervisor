package cn.edu.bnuz.supervisor


import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(SupervisorViewInterceptor)
class SupervisorViewInterceptorSpec extends Specification {

    def setup() {
    }

    def cleanup() {

    }

    void "Test supervisorView interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"supervisorView")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
