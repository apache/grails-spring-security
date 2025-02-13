package grails.plugin.springsecurity.rest

import grails.plugin.springsecurity.SecurityFilterPosition
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.rest.token.generation.SecureRandomTokenGenerator
import grails.plugin.springsecurity.rest.token.storage.GrailsCacheTokenStorageService
import grails.plugins.Plugin

class SpringSecurityRestGrailsCacheGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    String grailsVersion = "6.1.1 > *"
    List loadAfter = ['springSecurityRest']
    List pluginExcludes = [
        "grails-app/views/**"
    ]

    String title = "Spring Security REST Plugin - Grails cache support"
    String author = "Alvaro Sanchez-Mariscal"
    String authorEmail = ""
    String description = 'Implements authentication for REST APIs based on Spring Security. It uses a token-based workflow'

    def profiles = ['web']

    // URL to the plugin's documentation
    String documentation = "https://grails-plugins.github.io/grails-spring-security-rest/"

    // Extra (optional) plugin metadata
    String license = "APACHE"
    def organization = [name: 'Grails', url: 'https://www.grails.org/']

    def issueManagement = [ system: "GitHub", url: "https://github.com/grails/grails-spring-security-rest/issues" ]
    def scm = [ url: "https://github.com/grails/grails-spring-security-rest" ]

    Closure doWithSpring() { {->
        def conf = SpringSecurityUtils.securityConfig
        if (!conf || !conf.active || !conf.rest.active) {
            return
        }

        boolean printStatusMessages = (conf.printStatusMessages instanceof Boolean) ? conf.printStatusMessages : true

        if (printStatusMessages) {
            println '\t... with Grails cache support'
        }

        SpringSecurityUtils.loadSecondaryConfig 'DefaultRestGrailsCacheSecurityConfig'
        conf = SpringSecurityUtils.securityConfig

        SpringSecurityUtils.registerFilter 'restLogoutFilter', SecurityFilterPosition.LOGOUT_FILTER.order - 1

        tokenStorageService(GrailsCacheTokenStorageService) {
            grailsCacheManager = ref('grailsCacheManager')
            cacheName = conf.rest.token.storage.grailsCacheName
        }

        tokenGenerator(SecureRandomTokenGenerator)

    }}

    void doWithApplicationContext() {
        def conf = SpringSecurityUtils.securityConfig
        if (!conf || !conf.active || !conf.rest.active) {
            return
        }

        applicationContext.getBean(RestAuthenticationProvider).useJwt = false
    }
}
