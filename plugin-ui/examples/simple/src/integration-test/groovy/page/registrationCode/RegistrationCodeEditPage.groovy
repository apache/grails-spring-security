package page.registrationCode

import geb.module.TextInput
import page.EditPage

class RegistrationCodeEditPage extends EditPage {

	static url = 'registrationCode/edit'
	static typeName = { 'RegistrationCode' }
	static content = {
		token { $(name: 'token').module(TextInput) }
		username { $('#username').module(TextInput) }
	}
}
