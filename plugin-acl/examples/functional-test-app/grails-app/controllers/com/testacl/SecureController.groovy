package com.testacl

import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileStatic

@CompileStatic
class SecureController {

	@Secured('ROLE_ADMIN')
	def admins() {
		render 'Logged in with ROLE_ADMIN'
	}

	@Secured('ROLE_USER')
	def users() {
		render 'Logged in with ROLE_USER'
	}
}
