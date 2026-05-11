package page.user

import groovy.transform.Immutable

import geb.module.Checkbox
import geb.module.PasswordInput
import geb.module.TextInput
import page.LifecyclePage

@Immutable
class UserForm {

    String username
    String password
    Boolean enabled
    Boolean accountExpired
    Boolean accountLocked
    Boolean passwordExpired

    <P extends LifecyclePage> void applyTo(P page) {
        if (username != null) page.$('#username').module(TextInput).text = username
        if (password != null) page.$('#password').module(PasswordInput).text = password
        if (enabled != null) updateCheckbox(page.$(name: 'enabled').module(Checkbox), enabled)
        if (accountExpired != null) updateCheckbox(page.$(name: 'accountExpired').module(Checkbox), accountExpired)
        if (accountLocked != null) updateCheckbox(page.$(name: 'accountLocked').module(Checkbox), accountLocked)
        if (passwordExpired != null) updateCheckbox(page.$(name: 'passwordExpired').module(Checkbox), passwordExpired)
    }

    private static void updateCheckbox(Checkbox checkbox, Boolean value) {
        if (value) checkbox.check() else checkbox.uncheck()
    }
}
