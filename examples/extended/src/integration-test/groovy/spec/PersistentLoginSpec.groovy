package spec

import grails.testing.mixin.integration.Integration
import page.persistentLogin.PersistentLoginSearchPage
import spock.lang.Stepwise

@Stepwise
@Integration
class PersistentLoginSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		to PersistentLoginSearchPage

		then:
		assertNotSearched()

		when:
		submit()

		then:
		at PersistentLoginSearchPage
		assertResults 1, 10, 20
	}

	void testFindByUsername() {
		when:
		to PersistentLoginSearchPage
		username = '3'
		submit()

		then:
		at PersistentLoginSearchPage
		assertResults 1, 2, 2

		assertContentContains 'persistent_login_test_3'
		assertContentContains 'persistent_login_test_13'

		assertContentContains 'series3'
		assertContentContains 'series13'
	}

	void testFindByToken() {
		when:
		to PersistentLoginSearchPage
		token = '3'
		submit()

		then:
		at PersistentLoginSearchPage
		assertResults 1, 2, 2

		assertContentContains 'token13'
		assertContentContains 'token3'
	}

	void testFindBySeries() {
		when:
		to PersistentLoginSearchPage
		series = '4'
		submit()

		then:
		at PersistentLoginSearchPage
		assertResults 1, 2, 2

		assertContentContains 'series4'
		assertContentContains 'series14'
		assertContentContains 'persistent_login_test_4'
		assertContentContains 'persistent_login_test_14'
	}
}
