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
		def page = to(RequestmapSearchPage)

		then:
		page.assertNotSearched()

		when:
		page.submit()
		page = at(RequestmapSearchPage)

		then:
		waitFor { // wait for the page to re-load and display results
			page.assertResults(1, 3, 3)
		}
		with(pageSource) {
			contains('/secure/**')
			contains('ROLE_ADMIN')
			contains('/j_spring_security_switch_user')
			contains('ROLE_RUN_AS')
			contains('/**')
			contains('permitAll')
		}
	}

	void testFindByConfigAttribute() {
		when:
		to(RequestmapSearchPage).with {
			configAttribute = 'run'
			submit()
		}
		def page = at(RequestmapSearchPage)

		then:
		waitFor { // wait for the page to re-load and display results
			page.assertResults(1, 1, 1)
		}
		with(pageSource) {
			contains('/j_spring_security_switch_user')
			contains('ROLE_RUN_AS')
		}
	}

	void testFindByUrl() {
		when:
		to(RequestmapSearchPage).with {
			urlPattern = 'secure'
			submit()
		}
		def page = at(RequestmapSearchPage)

		then:
		waitFor { // wait for the page to re-load and display results
			page.assertResults(1, 1, 1)
		}
		with(pageSource) {
			contains('/secure/**')
			contains('ROLE_ADMIN')
		}
	}

	void testUniqueUrl() {
		when:
		to(RequestmapCreatePage).with {
			urlPattern = '/secure/**'
			configAttribute = 'ROLE_FOO'
			submit()
		}
		at(RequestmapCreatePage)

		then:
		waitFor { // wait for the page to re-load and display validation errors
			pageSource.contains('must be unique')
		}
	}

	void testCreateAndEdit() {
		given:
		def newPattern = "/foo/${UUID.randomUUID()}"

		// make sure it doesn't exist
		when:
		to(RequestmapSearchPage).with {
			urlPattern = newPattern
			submit()
		}
		def page = at(RequestmapSearchPage)

		then:
		waitFor { page.assertNoResults() }

		// create
		when:
		to(RequestmapCreatePage).with {
			urlPattern = newPattern
			configAttribute = 'ROLE_FOO'
			submit()
		}
		page = at(RequestmapEditPage)

		then:
		page.urlPattern.text == newPattern

		// edit
		when:
		page.urlPattern = "${newPattern}/new"
		page.submit()
		page = at(RequestmapEditPage)

		then:
		waitFor { // wait for the page to re-load and display updated values
			page.urlPattern.text == "${newPattern}/new"
		}

		// delete
		when:
		page.delete()
		page = at(RequestmapSearchPage)

		and:
		page.urlPattern = "${newPattern}/new"
		page.submit()
		page = at(RequestmapSearchPage)

		then:
		waitFor { // wait for the page to re-load and display results}
			page.assertNoResults()
		}
	}
}
