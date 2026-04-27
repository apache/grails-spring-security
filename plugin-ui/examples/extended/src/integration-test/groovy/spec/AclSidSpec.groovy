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
import page.aclSid.AclSidCreatePage
import page.aclSid.AclSidEditPage
import page.aclSid.AclSidSearchPage

@Integration
class AclSidSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def page = to(AclSidSearchPage)

		then:
		page.assertNotSearched()

		when:
		page.submit()
		page = at(AclSidSearchPage)

		then:
		waitFor { // Wait for the search page to be reloaded
			page.assertResults(1, 3, 3)
		}
	}

	void testFindBySid() {
		when:
		to(AclSidSearchPage).with {
			search('user')
		}
		def page = at(AclSidSearchPage)

		then:
		waitFor { // Wait for the search page to be reloaded
			page.assertResults(1, 2, 2)
		}
		with(pageSource) {
			contains('user1')
			contains('user2')
		}
	}

	void testFindByPrincipal() {
		when:
		def searchPage = to(AclSidSearchPage)
		// Temporary workaround for problem with Geb RadioButtons module
		//searchPage.principal.checked = '1'
		$('input', type: 'radio', name: 'principal', value: '1').click()
		searchPage.submit()

		then:
		at(AclSidSearchPage)
		waitFor { // Wait for the search page to be reloaded
			pageSource.contains('user1')
			pageSource.contains('user2')
			pageSource.contains('admin')
		}
	}

	void testUniqueName() {
		when:
		to(AclSidCreatePage).with {
			create('user1', true)
		}

		then:
		at(AclSidCreatePage)
		waitFor { // Wait for the create page to be reloaded
			pageSource.contains('must be unique')
		}
	}

	void testCreateAndEdit() {
		given:
		def newName = "newuser${UUID.randomUUID()}"

		// make sure it doesn't exist
		when:
		to(AclSidSearchPage).with {
			sid = newName
			submit()
		}
		def page = at(AclSidSearchPage)

		then:
		waitFor { // Wait for the search page to be reloaded
			page.assertNoResults()
		}

		// create
		when:
		to(AclSidCreatePage).tap {
			create(newName, true)
		}
		page = at(AclSidEditPage)

		then:
		page.sid.text == newName
		page.principal.checked

		// edit
		when:
		page.sid = "${newName}_new"
		page.submit()
		page = at(AclSidEditPage)

		then:
		waitFor { // Wait for the edit page to be reloaded
			page.sid.text == "${newName}_new"
		}

		// delete
		when:
		page.delete()
		page = at(AclSidSearchPage)

		and:
		page.sid = "${newName}_new"
		page.submit()
		page = at(AclSidSearchPage)

		then:
		waitFor { // Wait for the search page to be reloaded
			page.assertNoResults()
		}
	}
}
