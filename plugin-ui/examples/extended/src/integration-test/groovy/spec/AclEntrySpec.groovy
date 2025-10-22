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
import page.aclEntry.AclEntryCreatePage
import page.aclEntry.AclEntryEditPage
import page.aclEntry.AclEntrySearchPage

@Integration
class AclEntrySpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def searchPage = to(AclEntrySearchPage)

		then:
		searchPage.assertNotSearched()

		when:
		searchPage.submit()
		searchPage = at(AclEntrySearchPage)

		then:
		searchPage.assertResults(1, 10, 275)
	}

	void testFindByOid() {
		when:
		to(AclEntrySearchPage).with {
			aclObjectIdentity = '60'
			submit()
		}
		def searchPage = at(AclEntrySearchPage)

		then:
		searchPage.assertResults(1, 3, 3)
		pageSource.contains('60')
		pageSource.contains('62')
		pageSource.contains('194')
		pageSource.contains('195')
		pageSource.contains('user1')
		pageSource.contains('admin')
		pageSource.contains('BasePermission[...............................R=1]')
		pageSource.contains('BasePermission[...........................A....=16]')
		!pageSource.contains('>user2</a>')
	}

	void testFindByAceOrder() {
		when:
		to(AclEntrySearchPage).with {
			aceOrder = '2'
			submit()
		}
		def searchPage = at(AclEntrySearchPage)

		then:
		searchPage.assertResults(1, 10, 67)
		pageSource.contains('75')
		pageSource.contains('76')
		pageSource.contains('78')
		pageSource.contains('80')
		pageSource.contains('82')
		pageSource.contains('87')
		pageSource.contains('89')
		pageSource.contains('91')
		pageSource.contains('93')
		pageSource.contains('95')
	}

	void testFindByMask() {
		when:
		to(AclEntrySearchPage).with {
			mask = '1'
			submit()
		}
		def searchPage = at(AclEntrySearchPage)

		then:
		searchPage.assertResults(1, 10, 172)
	}

	void testUniqueOrder() {
		when:
		to(AclEntryCreatePage).with {
			aclObjectIdentityId = '3'
			aceOrder = '1'
			sid.selected = '1'
			mask = '1'
			submit()
		}

		then:
		at(AclEntryCreatePage)
		pageSource.contains('must be unique')
	}

	void testCreateAndEdit() {
		given:
		String newOrder = Math.abs(new Random().nextInt())

		// make sure it doesn't exist
		when:
		to(AclEntrySearchPage).with {
			aclObjectIdentity = '10'
			aceOrder = newOrder
			submit()
		}
		def searchPage = at(AclEntrySearchPage)

		then:
		searchPage.assertNoResults()

		// create
		when:
		to(AclEntryCreatePage).with {
			aclObjectIdentityId = '10'
			aceOrder = newOrder
			sid.selected = '2'
			mask = '2'
			submit()
		}
		def editPage = at(AclEntryEditPage)

		then:
		editPage.aceOrder.text == newOrder

		// edit
		when:
		editPage.aceOrder = ((newOrder as int) + 1) as String
		editPage.submit()
		editPage = at(AclEntryEditPage)

		then:
		editPage.aceOrder.text == ((newOrder as int) + 1) as String

		// delete
		when:
		editPage.delete()
		at(AclEntrySearchPage).with {
			aceOrder = ((newOrder as int) + 1) as String
			submit()
		}
		searchPage = at(AclEntrySearchPage)

		then:
		searchPage.assertNoResults()
	}
}
