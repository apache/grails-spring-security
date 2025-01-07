package spec

import grails.testing.mixin.integration.Integration
import page.role.RoleCreatePage
import page.role.RoleEditPage
import page.role.RoleSearchPage

@Integration
class RoleSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def roleSearchPage = browser.to(RoleSearchPage)

		then:
		roleSearchPage.assertNotSearched()

		when:
		roleSearchPage.submit()

		then:
		browser.at(RoleSearchPage)
		roleSearchPage.assertResults(1, 10, 12)
		assertContentContains('ROLE_COFFEE')
	}

	void testFindByAuthority() {
		when:
		def roleSearchPage = browser.to(RoleSearchPage).tap {
			search('ad')
		}

		then:
		browser.at(RoleSearchPage)
		roleSearchPage.assertResults(1, 2, 2)

		assertContentContains('ROLE_ADMIN')
		assertContentContains('ROLE_INSTEAD')
	}

	void testUniqueName() {
		when:
		def roleCreatePage = browser.to(RoleCreatePage).tap {
			create('ROLE_ADMIN')
		}

		then:
		browser.at(RoleCreatePage)
		roleCreatePage.assertNotUnique()
	}

	void testCreateAndEdit() {
		given:
		String newName = "ROLE_NEW_TEST${UUID.randomUUID()}"

		// make sure it doesn't exist
		when:
		def roleSearchPage = browser.to(RoleSearchPage).tap {
			search(newName)
		}

		then:
		roleSearchPage.assertNoResults()

		// create
		when:
		def roleCreatePage = browser.to(RoleCreatePage).tap {
			create(newName)
		}

		then:
		def roleEditPage = browser.at(RoleEditPage)
		roleEditPage.authority.text == newName

		// edit
		when:
		roleEditPage.update("${newName}_new")

		then:
		browser.at(RoleEditPage)
		roleEditPage.authority.text == "${newName}_new"

		// delete
		when:
		roleEditPage.delete()

		then:
		browser.at(RoleSearchPage)

		when:
		roleSearchPage.search("${newName}_new")

		then:
		browser.at(RoleSearchPage)
		roleSearchPage.assertNoResults()
	}
}
