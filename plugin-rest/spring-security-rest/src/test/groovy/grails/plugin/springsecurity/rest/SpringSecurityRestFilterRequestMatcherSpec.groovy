package grails.plugin.springsecurity.rest

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockServletContext
import spock.lang.Specification
import spock.lang.Unroll

class SpringSecurityRestFilterRequestMatcherSpec extends Specification {

    @Unroll
    void "if the context path is #contextPath, the URI #uri matches: #matches"(String contextPath, String uri, boolean matches) {
        given:
        SpringSecurityRestFilterRequestMatcher requestMatcher = new SpringSecurityRestFilterRequestMatcher("/api/login")
        MockServletContext servletContext = new MockServletContext()
        servletContext.setContextPath(contextPath)
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext, "POST", uri)

        when:
        boolean matchesResult = requestMatcher.matches(request)

        then:
        matchesResult == matches

        where:
        contextPath         | uri                   || matches
        "/"                 | "/api/login"          || true
        "/"                 | "/api/loginxxx"       || false
        "/foo"              | "/api/login/foo"      || false
    }

}
