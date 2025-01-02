package spec

import grails.testing.mixin.integration.Integration
import page.role.RoleCreatePage
import page.role.RoleEditPage
import page.role.RoleSearchPage
import spock.lang.Stepwise

@Stepwise
@Integration
class RoleSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		to RoleSearchPage

		then:
		assertNotSearched()

		when:
		submit()

		then:
		at RoleSearchPage
		assertResults 1, 10, 12
		assertContentContains 'ROLE_COFFEE'
	}

	void testFindByAuthority() {
		when:
		to RoleSearchPage
		search 'ad'

		then:
		at RoleSearchPage
		assertResults 1, 2, 2

		assertContentContains 'ROLE_ADMIN'
		assertContentContains 'ROLE_INSTEAD'
	}

	void testUniqueName() {
		when:
		to RoleCreatePage
		create 'ROLE_ADMIN'

		then:
		at RoleCreatePage
		assertNotUnique()
	}

	void testCreateAndEdit() {
		given:
		String newName = 'ROLE_NEW_TEST' + UUID.randomUUID()

		// make sure it doesn't exist
		when:
		to RoleSearchPage
		search newName

		then:
		assertNoResults()

		// create
		when:
		to RoleCreatePage
		create newName

		then:
		at RoleEditPage
		authority == newName

		// edit
		when:
		update newName + '_new'

		then:
		at RoleEditPage
		authority == newName + '_new'

		// delete
		when:
		delete()

		then:
		at RoleSearchPage

		when:
		search newName + '_new'

		then:
		at RoleSearchPage
		assertNoResults()
	}
}
