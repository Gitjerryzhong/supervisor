package cn.edu.bnuz.supervisor


import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(PublicInterceptor)
class PublicInterceptorSpec extends Specification {

    def setup() {
    }

    def cleanup() {

    }

    void "Test public interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"public")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
