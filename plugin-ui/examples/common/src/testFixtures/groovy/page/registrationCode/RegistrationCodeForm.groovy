package page.registrationCode

import groovy.transform.Immutable

import geb.Page
import geb.module.TextInput

@Immutable
class RegistrationCodeForm {

    String token
    String username

    <P extends Page> void applyTo(P page) {
        if (token != null) page.$(name: 'token').module(TextInput).text = token
        if (username != null) page.$('#username').module(TextInput).text = username
    }
}
