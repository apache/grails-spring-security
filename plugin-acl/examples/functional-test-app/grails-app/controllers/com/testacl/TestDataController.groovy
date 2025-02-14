package com.testacl

import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileStatic

@CompileStatic
@Secured('permitAll')
class TestDataController {

	TestDataService testDataService

	def reset() {
		testDataService.reset()
		render 'OK'
	}
}
