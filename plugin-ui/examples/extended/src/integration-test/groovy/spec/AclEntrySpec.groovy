package spec

import grails.testing.mixin.integration.Integration
import page.aclEntry.AclEntryCreatePage
import page.aclEntry.AclEntryEditPage
import page.aclEntry.AclEntrySearchPage

@Integration
class AclEntrySpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def aclEntrySearchPage = browser.to(AclEntrySearchPage)

		then:
		aclEntrySearchPage.assertNotSearched()

		when:
		aclEntrySearchPage.submit()

		then:
		browser.at(AclEntrySearchPage)
		aclEntrySearchPage.assertResults(1, 10, 275)
	}

	void testFindByOid() {
		when:
		def aclEntrySearchPage = browser.to(AclEntrySearchPage).tap {
			aclObjectIdentity = '60'
			submit()
		}

		then:
		browser.at(AclEntrySearchPage)
		aclEntrySearchPage.assertResults(1, 3, 3)

		assertContentContains('60')
		assertContentContains('398')
		assertContentContains('399')
		assertContentContains('400')
		assertContentContains('user1')
		assertContentContains('admin')
		assertContentDoesNotContain('>user2</a>')
		assertContentContains('BasePermission[...............................R=1]')
		assertContentContains('BasePermission[...........................A....=16]')
	}

	void testFindByAceOrder() {
		when:
		def aclEntrySearchPage = browser.to(AclEntrySearchPage).tap {
			aceOrder = '2'
			submit()
		}

		then:
		browser.at(AclEntrySearchPage)
		aclEntrySearchPage.assertResults(1, 10, 67)
		['104', '111', '119', '126', '131', '136', '141', '146', '152', '159'].each {
			assertContentContains it
		}
	}

	void testFindByMask() {
		when:
		def aclEntrySearchPage = browser.to(AclEntrySearchPage).tap {
			mask = '1'
			submit()
		}

		then:
		browser.at(AclEntrySearchPage)
		aclEntrySearchPage.assertResults(1, 10, 172)
	}

	void testUniqueOrder() {
		when:
		def aclEntryCreatePage = browser.to(AclEntryCreatePage).tap {
			aclObjectIdentityId = '3'
			aceOrder = '1'
			sid.selected = '1'
			mask = '1'
			submit()
		}

		then:
		browser.at(AclEntryCreatePage)
		aclEntryCreatePage.assertNotUnique()
	}

	void testCreateAndEdit() {
		given:
		String newOrder = Math.abs(new Random().nextInt())

		// make sure it doesn't exist
		when:
		def aclEntrySearchPage = browser.to(AclEntrySearchPage).tap {
			aclObjectIdentity = '10'
			aceOrder = newOrder
			submit()
		}

		then:
		browser.at(AclEntrySearchPage)
		aclEntrySearchPage.assertNoResults()

		// create
		when:
		def aclEntryCreatePage = browser.to(AclEntryCreatePage).tap {
			aclObjectIdentityId = '10'
			aceOrder = newOrder
			sid.selected = '2'
			mask = '2'
			submit()
		}

		then:
		def aclEntryEditPage = browser.at(AclEntryEditPage)
		aclEntryEditPage.aceOrder.text == newOrder

		// edit
		when:
		aclEntryEditPage.aceOrder = ((newOrder as int) + 1) as String
		aclEntryEditPage.submit()

		then:
		browser.at(AclEntryEditPage)
		aclEntryEditPage.aceOrder.text == ((newOrder as int) + 1) as String

		// delete
		when:
		aclEntryEditPage.delete()

		then:
		browser.at(AclEntrySearchPage)

		when:
		aclEntrySearchPage.aceOrder = ((newOrder as int) + 1) as String
		aclEntrySearchPage.submit()

		then:
		browser.at(AclEntrySearchPage)
		aclEntrySearchPage.assertNoResults()
	}
}
