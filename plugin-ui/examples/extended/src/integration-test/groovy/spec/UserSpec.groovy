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

import page.user.UserCreatePage
import page.user.UserEditPage
import page.user.UserSearchPage
import spock.lang.Stepwise

import grails.testing.mixin.integration.Integration

@Stepwise
@Integration
class UserSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def page = to(UserSearchPage)

		then:
		page.assertNotSearched()

		when:
		page.submit()

		then:
		page.assertResults(1, 10, 22)
	}

	void testFindByUsername() {
		when:
		def page = to(UserSearchPage).tap {
			username.text = 'foo'
			submit()
		}

		then:
		page.assertResults(1, 3, 3)
		with(pageSource) {
			contains('foon_2')
			contains('foolkiller')
			contains('foostra')
		}
	}

	void testFindByDisabled() {
		when:
		def page = to(UserSearchPage)

		// Temporary workaround for problem with Geb RadioButtons module
		//searchPage.enabled.checked = '-1'
		$('input', type: 'radio', name: 'enabled', value: '-1').click()
		page.submit()
		page = at(UserSearchPage)

		then:
		page.assertResults(1, 1, 1)
		pageSource.contains('billy9494')
	}

	void testFindByAccountExpired() {
		when:
		def page = to(UserSearchPage)

		// Temporary workaround for problem with Geb RadioButtons module
		//searchPage.accountExpired.checked = '1'
		$('input', type: 'radio', name: 'accountExpired', value: '1').click()
		page.submit()
		page = at(UserSearchPage)

		then:
		page.assertResults(1, 3, 3)
		with(pageSource) {
			contains('maryrose')
			contains('ratuig')
			contains('rome20c')
		}
	}

	void testFindByAccountLocked() {
		when:
		def page = to(UserSearchPage)

		// Temporary workaround for problem with Geb RadioButtons module
		//searchPage.accountLocked.checked = '1'
		$('input', type: 'radio', name: 'accountLocked', value: '1').click()
		page.submit()
		page = at(UserSearchPage)

		then:
		page.assertResults(1, 3, 3)
		with(pageSource) {
			contains('aaaaaasd')
			contains('achen')
			contains('szhang1999')
		}
	}

	void testFindByPasswordExpired() {
		when:
		def page = to(UserSearchPage)

		// Temporary workaround for problem with Geb RadioButtons module
		//searchPage.passwordExpired.checked = '1'
		$('input', type: 'radio', name: 'passwordExpired', value: '1').click()
		page.submit()
		page = at(UserSearchPage)

		then:
		page.assertResults(1, 3, 3)
		with(pageSource) {
			contains('hhheeeaaatt')
			contains('mscanio')
			contains('kittal')
		}
	}

	void testCreateAndEdit() {
		given:
		def newUsername = "newuser${UUID.randomUUID()}"

		// make sure it doesn't exist
		when:
		def page = to(UserSearchPage).tap {
			username = newUsername
			submit()
		}

		then:
		page.assertNoResults()

		// create
		when:
		via(UserCreatePage).with {
			username = newUsername
			password = 'password'
			enabled.check()
			submit()
		}
		page = at(UserEditPage)

		then:
		with(page) {
			username.text == newUsername
			enabled.checked
			!accountExpired.checked
			!accountLocked.checked
			!passwordExpired.checked
		}

		// edit
		when:
		def updatedName = "${newUsername}_updated"
		page.with {
			username.text = updatedName
			enabled.uncheck()
			accountExpired.check()
			accountLocked.check()
			passwordExpired.check()
			submit()
		}
		page = at(UserEditPage)

		then:
		with(page) {
			username.text == updatedName
			!enabled.checked
			accountExpired.checked
			accountLocked.checked
			passwordExpired.checked
		}

		// delete
		when:
		page.delete()
		page = at(UserSearchPage)

		and:
		page.with {
			username.text = updatedName
			submit()
		}
		page = at(UserSearchPage)

		then:
		page.assertNoResults()
	}
}
