package grails.plugin.springsecurity.oauth2

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.oauth2.exception.OAuth2Exception
import grails.util.Holders

class SpringSecurityOauth2UrlMappings {

    static mappings = {
        def active = Holders.grailsApplication.config.getProperty('grails.plugin.springsecurity.oauth2.active', Boolean, true)
        def enabled = (active instanceof Boolean) ? active : true
        if (enabled && SpringSecurityUtils.securityConfig?.active) {
            "/oauth2/$provider/authenticate"(controller: 'springSecurityOAuth2', action: 'authenticate')
            "/oauth2/$provider/callback"(controller: 'springSecurityOAuth2', action: 'callback')
            "/oauth2/$provider/success"(controller: 'springSecurityOAuth2', action: 'onSuccess')
            "/oauth2/$provider/failure"(controller: 'springSecurityOAuth2', action: 'onFailure')
            "/oauth2/ask"(controller: 'springSecurityOAuth2', action: 'ask')
            "/oauth2/linkaccount"(controller: 'springSecurityOAuth2', action: 'linkAccount')
            "/oauth2/createaccount"(controller: 'springSecurityOAuth2', action: 'createAccount')
            '500'(controller: 'login', action: 'auth', exception: OAuth2Exception)
        }
    }
}
