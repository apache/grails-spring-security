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
import page.role.RoleCreatePage
import page.role.RoleEditPage
import page.role.RoleSearchPage

@Integration
class RoleSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def searchPage = to(RoleSearchPage)

		then:
		searchPage.assertNotSearched()

		when:
		searchPage.submit()
		searchPage = at(RoleSearchPage)

		then:
		searchPage.assertResults(1, 10, 12)
		pageSource.contains('ROLE_COFFEE')
	}

	void testFindByAuthority() {
		when:
		to(RoleSearchPage).with {
			search('ad')
		}
		def searchPage = at(RoleSearchPage)

		then:
		searchPage.assertResults(1, 2, 2)
		pageSource.contains('ROLE_ADMIN')
		pageSource.contains('ROLE_INSTEAD')
	}

	void testUniqueName() {
		when:
		to(RoleCreatePage).with {
			create('ROLE_ADMIN')
		}

		then:
		at(RoleCreatePage)
		pageSource.contains('must be unique')
	}

	void testCreateAndEdit() {
		given:
		String newName = "ROLE_NEW_TEST${UUID.randomUUID()}"

		// make sure it doesn't exist
		when:
		to(RoleSearchPage).tap {
			search(newName)
		}
		def searchPage = at(RoleSearchPage)

		then:
		searchPage.assertNoResults()

		// create
		when:
		to(RoleCreatePage).with {
			create(newName)
		}
		def editPage = at(RoleEditPage)

		then:
		editPage.authority.text == newName

		// edit
		when:
		editPage.update("${newName}_new")
		editPage = at(RoleEditPage)

		then:
		editPage.authority.text == "${newName}_new"

		// delete
		when:
		editPage.delete()
		searchPage = at(RoleSearchPage)

		and:
		searchPage.search("${newName}_new")
		searchPage = at(RoleSearchPage)

		then:
		searchPage.assertNoResults()
	}
}
