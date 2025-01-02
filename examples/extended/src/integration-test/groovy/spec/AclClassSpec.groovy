package spec

import grails.testing.mixin.integration.Integration
import page.aclClass.AclClassCreatePage
import page.aclClass.AclClassEditPage
import page.aclClass.AclClassSearchPage
import spock.lang.Stepwise

@Stepwise
@Integration
class AclClassSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		to AclClassSearchPage

		then:
		assertNotSearched()

		when:
		submit()

		then:
		at AclClassSearchPage
		assertResults 1, 1, 1
	}

	void testFindByName() {
		when:
		to AclClassSearchPage
		search 'report'

		then:
		at AclClassSearchPage
		assertResults 1, 1, 1
		assertContentContains 'test.Report'
	}

	void testUniqueName() {
		when:
		to AclClassCreatePage
		create 'test.Report'

		then:
		at AclClassCreatePage
		assertNotUnique()
	}

	void testCreateAndEdit() {
		given:
		String newName = 'com.some.domain.Clazz' + UUID.randomUUID()

		// make sure it doesn't exist
		when:
		to AclClassSearchPage
		search newName

		then:
		at AclClassSearchPage
		assertNoResults()

		// create
		when:
		to AclClassCreatePage
		create newName

		then:
		at AclClassEditPage
		className == newName

		// edit
		when:
		update newName + '_new'

		then:
		at AclClassEditPage
		className == newName + '_new'

		// delete
		when:
		delete()

		then:
		at AclClassSearchPage

		when:
		search newName + '_new'

		then:
		at AclClassSearchPage
		assertNoResults()
	}
}
