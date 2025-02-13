package page.register

import geb.module.TextInput
import page.AbstractSecurityPage

class SecurityQuestionsPage extends AbstractSecurityPage {

	static url = 'register/securityQuestions'
	static at = { title == 'Security Questions' }
	static content = {
		form { $('securityQuestionsForm') }
		question1 { $('#myAnswer1').module(TextInput) }
		question2 { $('#myAnswer2').module(TextInput) }
		submitBtn { $('a', id: 'submit') }
	}
}
