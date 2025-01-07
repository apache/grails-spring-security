package page.register

import geb.module.PasswordInput
import page.AbstractSecurityPage

class ResetPasswordPage extends AbstractSecurityPage {

    static url = 'register/resetPassword'
    static at = { title == 'Reset Password' }
    static content = {
        form { $('resetPasswordForm') }
        password { $('#password').module(PasswordInput) }
        password2 { $('#password2').module(PasswordInput) }
        submitBtn { $('a', id: 'submit') }
    }

    def enterNewPassword(String p1, String p2) {
        password = p1
        password2 = p2
        submit()
    }
}