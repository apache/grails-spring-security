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

import grails.testing.mixin.integration.Integration
import page.registrationCode.RegistrationCodeEditPage
import page.registrationCode.RegistrationCodeSearchPage

@Integration
class RegistrationCodeSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def searchPage = to(RegistrationCodeSearchPage)

		then:
		searchPage.assertNotSearched()

		when:
		searchPage.submit()
		searchPage = at(RegistrationCodeSearchPage)

		then:
		searchPage.assertResults(1, 10, 14)
		pageSource.contains('registration_test_2')
		pageSource.contains('0a154624f36d42e4aa68991a9477bd04')
	}

	void testFindByToken() {
		when:
		to(RegistrationCodeSearchPage).with {
			token = '4a7f88afec3746f7aab2f5d0d8df6d8e'
			submit()
		}
		def searchPage = at(RegistrationCodeSearchPage)

		then:
		searchPage.assertResults(1, 1, 1)
		pageSource.contains('registration_test_1')
		pageSource.contains('4a7f88afec3746f7aab2f5d0d8df6d8e')
	}

	void testFindByUsername() {
		when:
		to(RegistrationCodeSearchPage).tap {
			username = 'registration_test_3'
			submit()
		}
		def searchPage = at(RegistrationCodeSearchPage)

		then:
		searchPage.assertResults(1, 5, 5)
		pageSource.contains('registration_test_3')
		pageSource.contains('89f9bbc658b14808ae4c77c6e17e551a')
	}

	void testEdit() {
		when:
		go('registrationCode/edit/4')
		def editPage = at(RegistrationCodeEditPage)

		then:
		editPage.username.text == 'registration_test_1'
		editPage.token.text == 'a50e061e0e2f424fb7fbc2ff3dae597d'

		when:
		editPage.with {
			username = 'new_user'
			token = 'new_token'
			submit()
		}
		editPage = at(RegistrationCodeEditPage)

		then:
		editPage.username.text == 'new_user'
		editPage.token.text == 'new_token'

		when:
		go('registrationCode/edit/4')
		editPage = at(RegistrationCodeEditPage)

		then:
		editPage.username.text == 'new_user'
		editPage.token.text == 'new_token'
	}
}
