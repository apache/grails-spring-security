package grails.plugin.springsecurity.rest

import grails.plugin.springsecurity.SecurityFilterPosition
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.rest.token.generation.SecureRandomTokenGenerator
import grails.plugin.springsecurity.rest.token.storage.GormTokenStorageService
import grails.plugins.Plugin

class SpringSecurityRestGormGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    String grailsVersion = "6.1.1 > *"
    List loadAfter = ['springSecurityRest']
    List pluginExcludes = [
        "grails-app/views/**"
    ]

    String title = "Spring Security REST Plugin - GORM support"
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
            println '\t... with GORM support'
        }

        SpringSecurityUtils.loadSecondaryConfig 'DefaultRestGormSecurityConfig'
        conf = SpringSecurityUtils.securityConfig

        SpringSecurityUtils.registerFilter 'restLogoutFilter', SecurityFilterPosition.LOGOUT_FILTER.order - 1

        tokenStorageService(GormTokenStorageService) {
            userDetailsService = ref('userDetailsService')
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
