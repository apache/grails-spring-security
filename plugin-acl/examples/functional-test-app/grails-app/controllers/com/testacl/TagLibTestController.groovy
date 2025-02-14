package com.testacl

import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic
class TagLibTestController {

	@CompileDynamic
	@Secured('permitAll')
	def test() {
		[reportIdsAndNumbers: [1, 13, 80].collect { [id: Report.findByNumber(it).id, number: it] }]
	}
}
