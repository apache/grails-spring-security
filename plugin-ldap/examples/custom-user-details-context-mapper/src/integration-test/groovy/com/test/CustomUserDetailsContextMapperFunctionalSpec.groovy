/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package com.test

import grails.plugin.geb.ContainerGebConfiguration
import grails.testing.mixin.integration.Integration
import pages.IndexPage
import pages.SecureSuperuserPage
import pages.SecureUserPage

@Integration
@ContainerGebConfiguration(reporting = true)
class CustomUserDetailsContextMapperFunctionalSpec extends AbstractSecurityFunctionalSpec {

	void 'secured urls are not visible without auth'() {
		when:
		go SecureUserPage.url

		then:
		assertContentContains 'Please Login'

		when:
		go SecureSuperuserPage.url

		then:
		assertContentContains 'Please Login'
	}

	def "login with a user present in the database"() {
		when:
		go SecureUserPage.url

		then:
		assertContentContains 'Please Login'

		when:
		report("At Login")
		login 'jane', 'password'

		then:
		at SecureUserPage
		report("At secured")

		and:
		assertContentContains('jane@example.com')

		when:
		logout()

		then:
		at IndexPage
	}

}
