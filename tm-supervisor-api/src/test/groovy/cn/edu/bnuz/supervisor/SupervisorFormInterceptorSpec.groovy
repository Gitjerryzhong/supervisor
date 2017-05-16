package cn.edu.bnuz.supervisor


import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(SupervisorFormInterceptor)
class SupervisorFormInterceptorSpec extends Specification {

    def setup() {
    }

    def cleanup() {

    }

    void "Test supervisorForm interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"supervisorForm")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
