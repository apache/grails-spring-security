package spec

import grails.testing.mixin.integration.Integration
import page.requestmap.RequestmapCreatePage
import page.requestmap.RequestmapEditPage
import page.requestmap.RequestmapSearchPage
import spock.lang.Stepwise

@Stepwise
@Integration
class RequestmapSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		to RequestmapSearchPage

		then:
		assertNotSearched()

		when:
		submit()

		then:
		at RequestmapSearchPage
		assertResults 1, 3, 3
		assertContentContains '/secure/**'
		assertContentContains 'ROLE_ADMIN'
		assertContentContains '/j_spring_security_switch_user'
		assertContentContains 'ROLE_RUN_AS'
		assertContentContains '/**'
		assertContentContains 'permitAll'
	}

	void testFindByConfigAttribute() {
		when:
		to RequestmapSearchPage
		configAttribute = 'run'
		submit()

		then:
		at RequestmapSearchPage
		assertResults 1, 1, 1
		assertContentContains '/j_spring_security_switch_user'
		assertContentContains 'ROLE_RUN_AS'
	}

	void testFindByUrl() {
		when:
		to RequestmapSearchPage
		urlPattern = 'secure'
		submit()

		then:
		at RequestmapSearchPage
		assertResults 1, 1, 1
		assertContentContains '/secure/**'
		assertContentContains 'ROLE_ADMIN'
	}

	void testUniqueUrl() {
		when:
		to RequestmapCreatePage
		urlPattern = '/secure/**'
		configAttribute = 'ROLE_FOO'
		submit()

		then:
		at RequestmapCreatePage
		assertNotUnique()
	}

	void testCreateAndEdit() {
		given:
		String newPattern = '/foo/' + UUID.randomUUID()

		// make sure it doesn't exist
		when:
		to RequestmapSearchPage
		urlPattern = newPattern
		submit()

		then:
		assertNoResults()

		// create
		when:
		to RequestmapCreatePage
		urlPattern = newPattern
		configAttribute = 'ROLE_FOO'
		submit()

		then:
		at RequestmapEditPage
		urlPattern == newPattern

		// edit
		when:
		urlPattern = newPattern + '/new'
		submit()

		then:
		at RequestmapEditPage
		urlPattern == newPattern + '/new'

		// delete
		when:
		delete()

		then:
		at RequestmapSearchPage

		when:
		urlPattern = newPattern + '/new'
		submit()

		then:
		at RequestmapSearchPage
		assertNoResults()
	}
}
