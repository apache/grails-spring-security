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
import page.aclClass.AclClassCreatePage
import page.aclClass.AclClassEditPage
import page.aclClass.AclClassSearchPage

@Integration
class AclClassSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def searchPage = to(AclClassSearchPage)

		then:
		searchPage.assertNotSearched()

		when:
		searchPage.submit()
		searchPage = at(AclClassSearchPage)

		then:
		searchPage.assertResults(1, 1, 1)
	}

	void testFindByName() {
		when:
		def searchPage = to(AclClassSearchPage).tap {
			search('report')
		}

		then:
		searchPage.assertResults(1, 1, 1)
		pageSource.contains('test.Report')
	}

	void testUniqueName() {
		when:
		to(AclClassCreatePage).tap {
			create('test.Report')
		}

		then:
		pageSource.contains('must be unique')
	}

	void testCreateAndEdit() {
		given:
		String newName = "com.some.domain.Clazz${UUID.randomUUID()}"

		// make sure it doesn't exist
		when:
		def searchPage = to(AclClassSearchPage).tap {
			search(newName)
		}

		then:
		searchPage.assertNoResults()

		// create
		when:
		to(AclClassCreatePage).with {
			create(newName)
		}
		def editPage = at(AclClassEditPage)

		then:
		editPage.className.text == newName

		// edit
		when:
		editPage.update("${newName}_new")
		editPage = at(AclClassEditPage)

		then:
		editPage.className.text == "${newName}_new"

		// delete
		when:
		editPage.delete()
		searchPage = at(AclClassSearchPage)
		searchPage.search("${newName}_new")
		searchPage = at(AclClassSearchPage)

		then:
		searchPage.assertNoResults()
	}
}
