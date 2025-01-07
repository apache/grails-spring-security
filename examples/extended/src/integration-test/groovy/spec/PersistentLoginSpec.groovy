package spec

import grails.testing.mixin.integration.Integration
import page.persistentLogin.PersistentLoginSearchPage

@Integration
class PersistentLoginSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		def persistentLoginSearchPage = browser.to(PersistentLoginSearchPage)

		then:
		persistentLoginSearchPage.assertNotSearched()

		when:
		persistentLoginSearchPage.submit()

		then:
		browser.at(PersistentLoginSearchPage)
		persistentLoginSearchPage.assertResults(1, 10, 20)
	}

	void testFindByUsername() {
		when:
		def persistentLoginSearchPage = browser.to(PersistentLoginSearchPage).tap {
			username = '3'
			submit()
		}

		then:
		browser.at(PersistentLoginSearchPage)
		persistentLoginSearchPage.assertResults(1, 2, 2)

		assertContentContains('persistent_login_test_3')
		assertContentContains('persistent_login_test_13')

		assertContentContains('series3')
		assertContentContains('series13')
	}

	void testFindByToken() {
		when:
		def persistenLoginSearchPage = browser.to(PersistentLoginSearchPage).tap {
			token = '3'
			submit()
		}

		then:
		browser.at(PersistentLoginSearchPage)
		persistenLoginSearchPage.assertResults(1, 2, 2)

		assertContentContains('token13')
		assertContentContains('token3')
	}

	void testFindBySeries() {
		when:
		def persistentLoginSearchPage = browser.to(PersistentLoginSearchPage).tap {
			series = '4'
			submit()
		}

		then:
		browser.at(PersistentLoginSearchPage)
		persistentLoginSearchPage.assertResults(1, 2, 2)

		assertContentContains('series4')
		assertContentContains('series14')
		assertContentContains('persistent_login_test_4')
		assertContentContains('persistent_login_test_14')
	}
}
