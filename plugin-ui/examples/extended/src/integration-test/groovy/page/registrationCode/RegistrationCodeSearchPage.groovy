package page.registrationCode

import geb.module.TextInput
import page.SearchPage

class RegistrationCodeSearchPage extends SearchPage {

	static url = 'registrationCode/search'
	static typeName = { 'Registration Code' }
	static content = {
		token { $(name: 'token').module(TextInput) }
		username { $('#username').module(TextInput) }
	}
}
