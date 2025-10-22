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
import page.persistentLogin.PersistentLoginSearchPage

@Integration
class PersistentLoginSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def searchPage = to(PersistentLoginSearchPage)

		then:
		searchPage.assertNotSearched()

		when:
		searchPage.submit()
		searchPage = at(PersistentLoginSearchPage)

		then:
		searchPage.assertResults(1, 10, 20)
	}

	void testFindByUsername() {
		when:
		to(PersistentLoginSearchPage).with {
			username = '3'
			submit()
		}
		def searchPage = at(PersistentLoginSearchPage)

		then:
		searchPage.assertResults(1, 2, 2)

		pageSource.contains('persistent_login_test_3')
		pageSource.contains('persistent_login_test_13')
		pageSource.contains('series3')
		pageSource.contains('series13')
	}

	void testFindByToken() {
		when:
		to(PersistentLoginSearchPage).with {
			token = '3'
			submit()
		}
		def searchPage = at(PersistentLoginSearchPage)

		then:
		searchPage.assertResults(1, 2, 2)
		pageSource.contains('token13')
		pageSource.contains('token3')
	}

	void testFindBySeries() {
		when:
		to(PersistentLoginSearchPage).with {
			series = '4'
			submit()
		}
		def searchPage = at(PersistentLoginSearchPage)

		then:
		searchPage.assertResults(1, 2, 2)

		pageSource.contains('series4')
		pageSource.contains('series14')
		pageSource.contains('persistent_login_test_4')
		pageSource.contains('persistent_login_test_14')
	}
}
