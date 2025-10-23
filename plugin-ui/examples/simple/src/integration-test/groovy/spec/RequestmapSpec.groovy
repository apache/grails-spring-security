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
import page.requestmap.RequestmapCreatePage
import page.requestmap.RequestmapEditPage
import page.requestmap.RequestmapSearchPage

@Integration
class RequestmapSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def searchPage = to(RequestmapSearchPage)

		then:
		searchPage.assertNotSearched()

		when:
		searchPage.submit()
		searchPage = at(RequestmapSearchPage)

		then:
		searchPage.assertResults(1, 3, 3)
		pageSource.contains('/secure/**')
		pageSource.contains('ROLE_ADMIN')
		pageSource.contains('/j_spring_security_switch_user')
		pageSource.contains('ROLE_RUN_AS')
		pageSource.contains('/**')
		pageSource.contains('permitAll')
	}

	void testFindByConfigAttribute() {
		when:
		to(RequestmapSearchPage).with {
			configAttribute = 'run'
			submit()
		}
		def searchPage =at(RequestmapSearchPage)

		then:
		searchPage.assertResults(1, 1, 1)
		pageSource.contains('/j_spring_security_switch_user')
		pageSource.contains('ROLE_RUN_AS')
	}

	void testFindByUrl() {
		when:
		to(RequestmapSearchPage).with {
			urlPattern = 'secure'
			submit()
		}
		def searchPage = at(RequestmapSearchPage)

		then:
		searchPage.assertResults(1, 1, 1)
		pageSource.contains('/secure/**')
		pageSource.contains('ROLE_ADMIN')
	}

	void testUniqueUrl() {
		when:
		to(RequestmapCreatePage).with {
			urlPattern = '/secure/**'
			configAttribute = 'ROLE_FOO'
			submit()
		}
		def createPage = at(RequestmapCreatePage)

		then:
		createPage.assertNotUnique()
	}

	void testCreateAndEdit() {
		given:
		String newPattern = "/foo/${UUID.randomUUID()}"

		// make sure it doesn't exist
		when:
		to(RequestmapSearchPage).with {
			urlPattern = newPattern
			submit()
		}
		def searchPage = at(RequestmapSearchPage)

		then:
		searchPage.assertNoResults()

		// create
		when:
		to(RequestmapCreatePage).with {
			urlPattern = newPattern
			configAttribute = 'ROLE_FOO'
			submit()
		}
		def editPage = at(RequestmapEditPage)

		then:
		editPage.urlPattern.text == newPattern

		// edit
		when:
		editPage.with {
			urlPattern = "$newPattern/new"
			submit()
		}
		editPage = at(RequestmapEditPage)

		then:
		editPage.urlPattern.text == "$newPattern/new"

		// delete
		when:
		editPage.delete()
		searchPage = at(RequestmapSearchPage)

		and:
		searchPage.urlPattern = "$newPattern/new"
		searchPage.submit()
		searchPage = at(RequestmapSearchPage)

		then:
		searchPage.assertNoResults()
	}
}
