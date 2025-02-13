package com.testacl

import grails.plugin.springsecurity.acl.annotation.AclVoter
import grails.plugin.springsecurity.acl.annotation.AclVoters
import groovy.transform.CompileStatic

@CompileStatic
@AclVoters([
	@AclVoter(name='aclReportWriteVoter',
	          configAttribute='ACL_REPORT_WRITE',
	          permissions=['ADMINISTRATION', 'WRITE']),
	@AclVoter(name='aclReportDeleteVoter',
	          configAttribute='ACL_REPORT_DELETE',
	          permissions=['ADMINISTRATION', 'DELETE'])
])
class Report {

	String name
	Integer number

	Report(String name, int number) {
		this.name = name
		this.number = number
	}
}
