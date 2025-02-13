package grails.plugin.springsecurity.oauth2

import grails.plugin.springsecurity.ReflectionUtils
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugins.Plugin
import groovy.util.logging.Slf4j
import org.slf4j.LoggerFactory

@Slf4j
class SpringSecurityOauth2GrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.1.8 > *"
    List loadAfter = ['spring-security-core']

    // TODO Fill in these fields
    def title = "Spring Security Oauth2" // Headline display name of the plugin
    def author = "Johannes Brunswicker"
    def authorEmail = "johannes.brunswicker@gmail.com"
    def description = '''\
This plugin provides the capability to authenticate via oauth. Depends on grails-spring-security-core.
'''
    def profiles = ['web']

    // URL to the plugin's documentation
//    def documentation = "http://grails.org/plugin/grails-spring-security-oauth2"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [[name: "Johannes Brunswicker", email: "johannes.brunswicker@gmail.com"]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]
    @Override
    Closure doWithSpring() {
        { ->
            ReflectionUtils.application = grailsApplication
            if (grailsApplication.warDeployed) {
                SpringSecurityUtils.resetSecurityConfig()
            }
            SpringSecurityUtils.application = grailsApplication

            // Check if there is an SpringSecurity configuration
            def coreConf = SpringSecurityUtils.securityConfig
            boolean printStatusMessages = (coreConf.printStatusMessages instanceof Boolean) ? coreConf.printStatusMessages : true
            if (!coreConf || !coreConf.active) {
                if (printStatusMessages) {
                    println("ERROR: There is no SpringSecurity configuration or SpringSecurity is disabled")
                    println("ERROR: Stopping configuration of SpringSecurity Oauth2")
                }
                return
            }

            if (printStatusMessages) {
                println("Configuring Spring Security OAuth2 plugin...")
            }

            SpringSecurityUtils.loadSecondaryConfig('DefaultSpringSecurityOAuth2Config')
            SpringSecurityUtils.securityConfig.controllerAnnotations.staticRules.add([pattern:'/oauth2/**', access:['permitAll']])

            grailsApplication.getArtefact('Domain','User')

            if (printStatusMessages) {
                println("... finished configuring Spring Security OAuth2\n")
            }
        }
    }

}
