package spec

import grails.testing.mixin.integration.Integration
import page.registrationCode.RegistrationCodeEditPage
import page.registrationCode.RegistrationCodeSearchPage
import spock.lang.Stepwise

@Stepwise
@Integration
class RegistrationCodeSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		to RegistrationCodeSearchPage

		then:
		assertNotSearched()

		when:
		submit()

		then:
		at RegistrationCodeSearchPage
		assertResults 1, 10, 14
		assertContentContains 'registration_test_2'
		assertContentContains '0a154624f36d42e4aa68991a9477bd04'
	}

	void testFindByToken() {
		when:
		to RegistrationCodeSearchPage
		token = '4a7f88afec3746f7aab2f5d0d8df6d8e'
		submit()

		then:
		at RegistrationCodeSearchPage
		assertResults 1, 1, 1
		assertContentContains 'registration_test_1'
		assertContentContains '4a7f88afec3746f7aab2f5d0d8df6d8e'
	}

	void testFindByUsername() {
		when:
		to RegistrationCodeSearchPage
		username = 'registration_test_3'
		submit()

		then:
		at RegistrationCodeSearchPage
		assertResults 1, 5, 5
		assertContentContains 'registration_test_3'
		assertContentContains '89f9bbc658b14808ae4c77c6e17e551a'
	}

	void testEdit() {
		when:
		go 'registrationCode/edit/4'

		then:
		at RegistrationCodeEditPage
		username == 'registration_test_1'
		token == 'a50e061e0e2f424fb7fbc2ff3dae597d'

		when:
		username = 'new_user'
		token = 'new_token'
		submit()

		then:
		at RegistrationCodeEditPage
		username == 'new_user'
		token == 'new_token'

		when:
		go 'registrationCode/edit/4'

		then:
		at RegistrationCodeEditPage
		username == 'new_user'
		token == 'new_token'
	}
}
