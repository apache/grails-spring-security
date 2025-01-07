package spec

import grails.testing.mixin.integration.Integration
import page.requestmap.RequestmapCreatePage
import page.requestmap.RequestmapEditPage
import page.requestmap.RequestmapSearchPage

@Integration
class RequestmapSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def requestmapSearchPage = browser.to(RequestmapSearchPage)

		then:
		requestmapSearchPage.assertNotSearched()

		when:
		requestmapSearchPage.submit()

		then:
		browser.at(RequestmapSearchPage)
		requestmapSearchPage.assertResults(1, 3, 3)
		assertContentContains('/secure/**')
		assertContentContains('ROLE_ADMIN')
		assertContentContains('/j_spring_security_switch_user')
		assertContentContains('ROLE_RUN_AS')
		assertContentContains('/**')
		assertContentContains('permitAll')
	}

	void testFindByConfigAttribute() {
		when:
		def requestmapSearchPage = browser.to(RequestmapSearchPage).tap {
			configAttribute = 'run'
			submit()
		}

		then:
		browser.at(RequestmapSearchPage)
		requestmapSearchPage.assertResults(1, 1, 1)
		assertContentContains('/j_spring_security_switch_user')
		assertContentContains('ROLE_RUN_AS')
	}

	void testFindByUrl() {
		when:
		def requestmapSearchPage = browser.to(RequestmapSearchPage).tap {
			urlPattern = 'secure'
			submit()
		}

		then:
		browser.at(RequestmapSearchPage)
		requestmapSearchPage.assertResults(1, 1, 1)
		assertContentContains('/secure/**')
		assertContentContains('ROLE_ADMIN')
	}

	void testUniqueUrl() {
		when:
		def requestmapCreatePage = browser.to(RequestmapCreatePage).tap {
			urlPattern = '/secure/**'
			configAttribute = 'ROLE_FOO'
			submit()
		}

		then:
		browser.at(RequestmapCreatePage)
		requestmapCreatePage.assertNotUnique()
	}

	void testCreateAndEdit() {
		given:
		String newPattern = "/foo/${UUID.randomUUID()}"

		// make sure it doesn't exist
		when:
		def requestmapSearchPage = browser.to(RequestmapSearchPage).tap {
			urlPattern = newPattern
			submit()
		}

		then:
		requestmapSearchPage.assertNoResults()

		// create
		when:
		def requestmapCreatePage = browser.to(RequestmapCreatePage).tap {
			urlPattern = newPattern
			configAttribute = 'ROLE_FOO'
			submit()
		}

		then:
		def requestmapEditPage = browser.at(RequestmapEditPage)
		requestmapEditPage.urlPattern.text == newPattern

		// edit
		when:
		requestmapEditPage.with {
			urlPattern = "$newPattern/new"
			submit()
		}

		then:
		browser.at(RequestmapEditPage)
		requestmapEditPage.urlPattern.text == "$newPattern/new"

		// delete
		when:
		requestmapEditPage.delete()

		then:
		browser.at(RequestmapSearchPage)

		when:
		requestmapSearchPage.urlPattern = "$newPattern/new"
		requestmapSearchPage.submit()

		then:
		browser.at(RequestmapSearchPage)
		requestmapSearchPage.assertNoResults()
	}
}
