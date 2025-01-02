package spec

import grails.testing.mixin.integration.Integration
import page.aclEntry.AclEntryCreatePage
import page.aclEntry.AclEntryEditPage
import page.aclEntry.AclEntrySearchPage
import spock.lang.Stepwise

@Stepwise
@Integration
class AclEntrySpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		to AclEntrySearchPage

		then:
		assertNotSearched()

		when:
		submit()

		then:
		at AclEntrySearchPage
		assertResults 1, 10, 275
	}

	void testFindByOid() {
		when:
		to AclEntrySearchPage
		aclObjectIdentity = '60'
		submit()

		then:
		at AclEntrySearchPage
		assertResults 1, 3, 3

		assertContentContains '60'
		assertContentContains '398'
		assertContentContains '399'
		assertContentContains '400'
		assertContentContains 'user1'
		assertContentContains 'admin'
		assertContentDoesNotContain '>user2</a>'
		assertContentContains 'BasePermission[...............................R=1]'
		assertContentContains 'BasePermission[...........................A....=16]'
	}

	void testFindByAceOrder() {
		when:
		to AclEntrySearchPage
		aceOrder = '2'
		submit()

		then:
		at AclEntrySearchPage
		assertResults 1, 10, 67
		['104', '111', '119', '126', '131', '136', '141', '146', '152', '159'].each { assertContentContains it }
	}

	void testFindByMask() {
		when:
		to AclEntrySearchPage
		mask = '1'
		submit()

		then:
		at AclEntrySearchPage
		assertResults 1, 10, 172
	}

	void testUniqueOrder() {
		when:
		to AclEntryCreatePage
		aclObjectIdentityId = '3'
		aceOrder = '2'
		sid.selected = '1'
		mask = '1'
		submit()

		then:
		at AclEntryCreatePage
		assertNotUnique()
	}

	void testCreateAndEdit() {
		given:
		String newOrder = Math.abs(new Random().nextInt())

		// make sure it doesn't exist
		when:
		to AclEntrySearchPage
		aclObjectIdentity = '10'
		aceOrder = newOrder
		submit()

		then:
		at AclEntrySearchPage
		assertNoResults()

		// create
		when:
		to AclEntryCreatePage
		aclObjectIdentityId = '10'
		aceOrder = newOrder
		sid.selected = '2'
		mask = '2'
		submit()

		then:
		at AclEntryEditPage
		aceOrder == newOrder

		// edit
		when:
		aceOrder = ((newOrder as int) + 1) as String
		submit()

		then:
		at AclEntryEditPage
		aceOrder == ((newOrder as int) + 1) as String

		// delete
		when:
		delete()

		then:
		at AclEntrySearchPage

		when:
		aceOrder = ((newOrder as int) + 1) as String
		submit()

		then:
		at AclEntrySearchPage
		assertNoResults()
	}
}
