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
import page.aclObjectIdentity.AclObjectIdentityCreatePage
import page.aclObjectIdentity.AclObjectIdentityEditPage
import page.aclObjectIdentity.AclObjectIdentitySearchPage

@Integration
class AclObjectIdentitySpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def searchPage = to(AclObjectIdentitySearchPage)

		then:
		searchPage.assertNotSearched()

		when:
		searchPage.submit()
		searchPage = at(AclObjectIdentitySearchPage)

		then:
		searchPage.assertResults(1, 10, 100)
	}

	void testFindById() {
		when:
		to(AclObjectIdentitySearchPage).with {
			objectId = '10'
			submit()
		}
		def searchPage = at(AclObjectIdentitySearchPage)

		then:
		searchPage.assertResults(1, 1, 1)
		pageSource.contains('test.Report')
	}

	void testFindByOwner() {
		when:
		to(AclObjectIdentitySearchPage).with {
			ownerId = '1'
			submit()
		}
		def searchPage = at(AclObjectIdentitySearchPage)

		then:
		searchPage.assertResults(1, 10, 98)
	}

	void testUniqueId() {
		when:
		to(AclObjectIdentityCreatePage).with {
			aclClass.selected = '1'
			objectId = 1
			ownerId.selected = '2'
			submit()
		}
		at(AclObjectIdentityCreatePage)

		then:
		pageSource.contains('must be unique')
	}

	void testCreateAndEdit() {
		given:
		String newId = Math.abs(new Random().nextInt())

		// make sure it doesn't exist
		when:
		to(AclObjectIdentitySearchPage).with {
			objectId = newId
			submit()
		}
		def searchPage = at(AclObjectIdentitySearchPage)

		then:
		searchPage.assertNoResults()

		// create
		when:
		to(AclObjectIdentityCreatePage).with {
			aclClass.selected = '1'
			objectId = newId
			ownerId.selected = '2'
			submit()
		}
		def editPage = at(AclObjectIdentityEditPage)

		then:
		editPage.objectId.text == newId

		// edit
		when:
		editPage.objectId = (newId.toInteger() + 1).toString()
		editPage.submit()
		editPage = at(AclObjectIdentityEditPage)

		then:
		editPage.objectId.text == (newId.toInteger() + 1).toString()

		// delete
		when:
		editPage.delete()
		at(AclObjectIdentitySearchPage).with {
			objectId = (newId.toInteger() + 1).toString()
			submit()
		}
		searchPage = at(AclObjectIdentitySearchPage)

		then:
		searchPage.assertNoResults()
	}
}
