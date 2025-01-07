package page.register

import geb.module.PasswordInput
import geb.module.TextInput
import page.AbstractSecurityPage

class RegisterPage extends AbstractSecurityPage {

	static url = 'register'
	static at = { title == 'Register' }
	static content = {
		form { $('registerForm') }
		username { $(name: 'username').module(TextInput) }
		email { $(name: 'email').module(TextInput) }
		password { $(name: 'password').module(PasswordInput) }
		password2 { $(name: 'password2').module(PasswordInput) }
		submitBtn { $('a', id: 'submit') }
	}
}
