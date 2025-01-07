package page.register

import geb.module.TextInput
import page.AbstractSecurityPage

class ForgotPasswordPage extends AbstractSecurityPage {

	static url = 'register/forgotPassword'
	static at = { title == 'Forgot Password' }
	static content = {
		form { $('forgotPasswordForm') }
		username { $(name: 'username').module(TextInput) }
		submitBtn { $('a', id: 'submit') }
	}
}
