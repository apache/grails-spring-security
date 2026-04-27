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
package spec

import page.registrationCode.RegistrationCodeEditPage
import page.registrationCode.RegistrationCodeSearchPage

import grails.testing.mixin.integration.Integration

@Integration
class RegistrationCodeSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def page = to(RegistrationCodeSearchPage)

		then:
		page.assertNotSearched()

		when:
		page.submit()
		page = at(RegistrationCodeSearchPage)

		then:
		waitFor {
			page.assertResults(1, 10, 14)
			pageSource.contains('registration_test_2')
			pageSource.contains('0a154624f36d42e4aa68991a9477bd04')
		}
	}

	void testFindByToken() {
		when:
		def page = to(RegistrationCodeSearchPage).tap {
			token.text = '4a7f88afec3746f7aab2f5d0d8df6d8e'
			submit()
		}
		page = at(RegistrationCodeSearchPage)

		then:
		waitFor {
			page.assertResults(1, 1, 1)
			pageSource.contains('registration_test_1')
			pageSource.contains('4a7f88afec3746f7aab2f5d0d8df6d8e')
		}
	}

	void testFindByUsername() {
		when:
		def page = to(RegistrationCodeSearchPage).tap {
			username.text = 'registration_test_3'
			submit()
		}
		page = at(RegistrationCodeSearchPage)

		then:
		waitFor {
			page.assertResults(1, 5, 5)
			pageSource.contains('registration_test_3')
			pageSource.contains('89f9bbc658b14808ae4c77c6e17e551a')
		}
	}

	void testEdit() {
		when:
		def page = to(RegistrationCodeEditPage, 4)

		then:
		with(page) {
			username.text == 'registration_test_1'
			token.text == 'a50e061e0e2f424fb7fbc2ff3dae597d'
		}

		when:
		page.with {
			username.text = 'new_user'
			token.text = 'new_token'
			submit()
		}
		page = at(RegistrationCodeEditPage)

		then:
		waitFor {
			page.username.text == 'new_user'
			page.token.text == 'new_token'
		}

		when:
		page = to(RegistrationCodeEditPage, 4)

		then:
		waitFor {
			page.username.text == 'new_user'
			page.token.text == 'new_token'
		}
	}
}
