package spring

import grails.ldap.server.TransientGrailsLdapServer

beans = {
    d1LdapServer(TransientGrailsLdapServer) {
        def conf = application.config.ldapServers.d1
        if (conf.base) {
            base = conf.base
        }
        if (conf.port) {
            port = conf.port
        }
        if (conf.indexed) {
            indexed = conf.indexed
        }
    }
}
