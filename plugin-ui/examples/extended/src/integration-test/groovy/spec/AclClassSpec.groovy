package spec

import grails.testing.mixin.integration.Integration
import page.aclClass.AclClassCreatePage
import page.aclClass.AclClassEditPage
import page.aclClass.AclClassSearchPage

@Integration
class AclClassSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def aclClassSearchPage = browser.to(AclClassSearchPage)

		then:
		aclClassSearchPage.assertNotSearched()

		when:
		aclClassSearchPage.submit()

		then:
		browser.at(AclClassSearchPage)
		aclClassSearchPage.assertResults(1, 1, 1)
	}

	void testFindByName() {
		when:
		def aclClassSearchPage = browser.to(AclClassSearchPage).tap {
			search('report')
		}

		then:
		browser.at(AclClassSearchPage)
		aclClassSearchPage.assertResults(1, 1, 1)
		assertContentContains('test.Report')
	}

	void testUniqueName() {
		when:
		def aclClassCreatePage = browser.to(AclClassCreatePage).tap {
			create('test.Report')
		}

		then:
		browser.at(AclClassCreatePage)
		aclClassCreatePage.assertNotUnique()
	}

	void testCreateAndEdit() {
		given:
		String newName = "com.some.domain.Clazz${UUID.randomUUID()}"

		// make sure it doesn't exist
		when:
		def aclClassSearchPage = browser.to(AclClassSearchPage).tap {
			search(newName)
		}

		then:
		browser.at(AclClassSearchPage)
		aclClassSearchPage.assertNoResults()

		// create
		when:
		browser.to(AclClassCreatePage).with {
			create(newName)
		}

		then:
		def aclClassEditPage = browser.at(AclClassEditPage)
		aclClassEditPage.className.text == newName

		// edit
		when:
		aclClassEditPage.update("${newName}_new")

		then:
		browser.at(AclClassEditPage)
		aclClassEditPage.className.text == "${newName}_new"

		// delete
		when:
		aclClassEditPage.delete()

		then:
		browser.at(AclClassSearchPage)

		when:
		aclClassSearchPage.search("${newName}_new")

		then:
		browser.at(AclClassSearchPage)
		aclClassSearchPage.assertNoResults()
	}
}
