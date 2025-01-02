package spec

import grails.testing.mixin.integration.Integration
import page.aclSid.AclSidCreatePage
import page.aclSid.AclSidEditPage
import page.aclSid.AclSidSearchPage
import spock.lang.Stepwise

@Stepwise
@Integration
class AclSidSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		to AclSidSearchPage

		then:
		assertNotSearched()

		when:
		submit()

		then:
		at AclSidSearchPage
		assertResults 1, 3, 3
	}

	void testFindBySid() {
		when:
		to AclSidSearchPage
		search 'user'

		then:
		at AclSidSearchPage
		assertResults 1, 2, 2

		assertContentContains 'user1'
		assertContentContains 'user2'
	}

	void testFindByPrincipal() {
		when:
		to AclSidSearchPage
		principal.checked = '1'
		submit()

		then:
		at AclSidSearchPage
		assertContentContains 'user1'
		assertContentContains 'user2'
		assertContentContains 'admin'
	}

	void testUniqueName() {
		when:
		to AclSidCreatePage
		create 'user1', true

		then:
		at AclSidCreatePage
		assertContentContains 'must be unique'
	}

	void testCreateAndEdit() {
		given:
		String newName = 'newuser' + UUID.randomUUID()

		// make sure it doesn't exist
		when:
		to AclSidSearchPage
		sid = newName
		submit()

		then:
		assertNoResults()

		// create
		when:
		to AclSidCreatePage
		create newName, true

		then:
		at AclSidEditPage
		sid == newName
		principal.checked

		// edit
		when:
		sid = newName + '_new'
		submit()

		then:
		at AclSidEditPage
		sid == newName + '_new'

		// delete
		when:
		delete()

		then:
		at AclSidSearchPage

		when:
		sid = newName + '_new'
		submit()

		then:
		at AclSidSearchPage
		assertNoResults()
	}
}
