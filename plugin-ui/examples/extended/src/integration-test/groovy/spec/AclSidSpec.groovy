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
		def searchPage = to(AclSidSearchPage)

		then:
		searchPage.assertNotSearched()

		when:
		searchPage.submit()
		searchPage = at(AclSidSearchPage)

		then:
		searchPage.assertResults(1, 3, 3)
	}

	void testFindBySid() {
		when:
		to(AclSidSearchPage).with {
			search('user')
		}
		def searchPage = at(AclSidSearchPage)

		then:
		searchPage.assertResults(1, 2, 2)
		pageSource.contains('user1')
		pageSource.contains('user2')
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
		pageSource.contains('user1')
		pageSource.contains('user2')
		pageSource.contains('admin')
	}

	void testUniqueName() {
		when:
		to(AclSidCreatePage).with {
			create('user1', true)
		}

		then:
		at(AclSidCreatePage)
		pageSource.contains('must be unique')
	}

	void testCreateAndEdit() {
		given:
		String newName = "newuser${UUID.randomUUID()}"

		// make sure it doesn't exist
		when:
		to(AclSidSearchPage).with {
			sid = newName
			submit()
		}
		def searchPage = at(AclSidSearchPage)

		then:
		searchPage.assertNoResults()

		// create
		when:
		to(AclSidCreatePage).tap {
			create(newName, true)
		}
		def editPage = at(AclSidEditPage)

		then:
		editPage.sid.text == newName
		editPage.principal.checked

		// edit
		when:
		editPage.sid = "${newName}_new"
		editPage.submit()
		editPage = at(AclSidEditPage)

		then:
		editPage.sid.text == "${newName}_new"

		// delete
		when:
		editPage.delete()
		searchPage = at(AclSidSearchPage)

		and:
		searchPage.sid = "${newName}_new"
		searchPage.submit()
		searchPage = at(AclSidSearchPage)

		then:
		searchPage.assertNoResults()
	}
}
