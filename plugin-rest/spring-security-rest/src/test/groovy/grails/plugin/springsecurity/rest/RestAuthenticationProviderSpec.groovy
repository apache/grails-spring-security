package grails.plugin.springsecurity.rest

import grails.plugin.springsecurity.rest.token.AccessToken
import grails.plugin.springsecurity.rest.token.generation.jwt.SignedJwtTokenGenerator
import grails.plugin.springsecurity.rest.token.storage.TokenNotFoundException
import grails.plugin.springsecurity.rest.token.storage.jwt.JwtTokenStorageService
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import spock.lang.Issue
import spock.lang.Specification

class RestAuthenticationProviderSpec extends Specification implements TokenGeneratorSupport {

    RestAuthenticationProvider restAuthenticationProvider
    SignedJwtTokenGenerator tokenGenerator


    void setup() {
        this.tokenGenerator = setupSignedJwtTokenGenerator()
        this.restAuthenticationProvider = new RestAuthenticationProvider(useJwt: true)
        UserDetailsService userDetailsService = new InMemoryUserDetailsManager([])
        UserDetails testUser = new User('testUser', 'testPassword', [])
        userDetailsService.createUser(testUser)

        JwtService jwtService = new JwtService(jwtSecret: this.tokenGenerator.jwtTokenStorageService.jwtService.jwtSecret)
        this.restAuthenticationProvider.jwtService = jwtService
        this.restAuthenticationProvider.tokenStorageService = new JwtTokenStorageService(jwtService: jwtService, userDetailsService: userDetailsService)
    }

    @Issue("https://github.com/grails/grails-spring-security-rest/issues/276")
    void "if the JWT's expiration time is null, it's validated successfully"() {
        given:
        AccessToken accessToken = tokenGenerator.generateAccessToken(new User('testUser', 'testPassword', []), 0)

        when:
        Authentication result = this.restAuthenticationProvider.authenticate(accessToken)

        then:
        result.authenticated
    }

    @Issue("https://github.com/grails/grails-spring-security-rest/issues/391")
    void "refresh tokens should not be usable for authentication"() {
        given:
        AccessToken accessToken = tokenGenerator.generateAccessToken(new User('testUser', 'testPassword', []), 0)
        accessToken.accessToken = accessToken.refreshToken

        when:
        this.restAuthenticationProvider.authenticate(accessToken)

        then:
        def e = thrown(TokenNotFoundException)
        e.message =~ /Token .* is not valid/
    }
}
