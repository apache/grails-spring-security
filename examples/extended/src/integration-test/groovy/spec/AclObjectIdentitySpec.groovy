package spec

import grails.testing.mixin.integration.Integration
import page.aclObjectIdentity.AclObjectIdentityCreatePage
import page.aclObjectIdentity.AclObjectIdentityEditPage
import page.aclObjectIdentity.AclObjectIdentitySearchPage
import spock.lang.Stepwise

@Stepwise
@Integration
class AclObjectIdentitySpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		to AclObjectIdentitySearchPage

		then:
		assertNotSearched()

		when:
		submit()

		then:
		at AclObjectIdentitySearchPage
		assertResults 1, 10, 100
	}

	void testFindById() {
		when:
		to AclObjectIdentitySearchPage
		objectId = '10'
		submit()

		then:
		at AclObjectIdentitySearchPage
		assertResults 1, 1, 1
		assertContentContains 'test.Report'
	}

	void testFindByOwner() {
		when:
		to AclObjectIdentitySearchPage
		ownerId = '1'
		submit()

		then:
		at AclObjectIdentitySearchPage
		assertResults 1, 10, 98
	}

	void testUniqueId() {
		when:
		to AclObjectIdentityCreatePage
		aclClass.selected = '1'
		objectId = 1
		ownerId.selected = '2'
		submit()

		then:
		at AclObjectIdentityCreatePage
		assertNotUnique()
	}

	void testCreateAndEdit() {
		given:
		String newId = Math.abs(new Random().nextInt())

		// make sure it doesn't exist
		when:
		to AclObjectIdentitySearchPage
		objectId = newId
		submit()

		then:
		assertNoResults()

		// create
		when:
		to AclObjectIdentityCreatePage
		aclClass.selected = '1'
		objectId = newId
		ownerId.selected = '2'
		submit()

		then:
		at AclObjectIdentityEditPage
		objectId == newId

		// edit
		when:
		objectId = (newId.toInteger() + 1).toString()
		submit()

		then:
		at AclObjectIdentityEditPage
		objectId == (newId.toInteger() + 1).toString()

		// delete
		when:
		delete()

		then:
		at AclObjectIdentitySearchPage

		when:
		objectId = (newId.toInteger() + 1).toString()
		submit()

		then:
		at AclObjectIdentitySearchPage
		assertNoResults()
	}
}
