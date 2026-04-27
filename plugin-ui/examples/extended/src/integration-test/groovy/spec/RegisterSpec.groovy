/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package spec

import page.profile.ProfileCreatePage
import page.profile.ProfileEditPage
import page.profile.ProfileListPage
import page.register.ForgotPasswordPage
import page.register.RegisterPage
import page.register.ResetPasswordPage
import page.register.SecurityQuestionsPage
import page.user.UserEditPage
import page.user.UserSearchPage

import grails.testing.mixin.integration.Integration

@Integration
class RegisterSpec extends AbstractSecuritySpec {

	void testRegisterValidation() {
		when:
		def page = to(RegisterPage).tap {
			submit()
		}

		then:
		waitFor { // We end up back at the register page, but we need to wait for the validation errors to be rendered
			pageSource.contains('Username is required')
			pageSource.contains('Email is required')
			pageSource.contains('Password is required')
		}

		when:
		page.with {
			username.text = 'admin'
			email.text = 'foo'
			password.text = 'abcdefghijk'
			password2.text = 'mnopqrstuwzy'
			submit()
		}

		then:
		waitFor { // We end up back at the register page, but we need to wait for the validation errors to be rendered
			pageSource.contains('The username is taken')
			pageSource.contains('Please provide a valid email address')
			pageSource.contains('Password must have at least one letter, number, and special character: !@#$%^&')
			pageSource.contains('Passwords do not match')
		}

		when:
		page.with {
			username.text = 'abcdef123'
			email.text = 'abcdef@abcdef.com'
			password.text = 'aaaaaaaa'
			password2.text = 'aaaaaaaa'
			submit()
		}

		then:
		waitFor { // We end up back at the register page, but we need to wait for the validation errors to be rendered
			pageSource.contains('Password must have at least one letter, number, and special character: !@#$%^&')
		}
	}

	void testForgotPasswordValidation() {
		when:
		def page = to(ForgotPasswordPage).tap {
			submit()
		}

		then:
		at(ForgotPasswordPage)
		waitFor { // We end up back at the forgot password page, but we need to wait for the validation errors to be rendered
			pageSource.contains('Please enter your username')
		}

		when:
		page.with {
			username.text = '1111'
			submit()
		}

		then:
		at(ForgotPasswordPage)
		waitFor { // We end up back at the forgot password page, but we need to wait for the validation errors to be rendered
			pageSource.contains('No user was found with that username')
		}
	}

	void testRegisterAndForgotPassword() {
		given:
		def un = "test_user_abcdef${System.currentTimeMillis()}"

		when:
		go('register/resetPassword?t=123')

		then:
		waitFor { pageSource.contains('Sorry, we have no record of that request, or it has expired') }

		when:
		to(RegisterPage).with {
			username = un
			email = "$un@abcdef.com"
			password = 'aaaaaa1#'
			password2 = 'aaaaaa1#'
			submit()
		}

		then:
		waitFor { pageSource.contains('Your registration is complete') }

		when:
		to(ProfileCreatePage).with {
			create(un)
		}
		def page = at(ProfileListPage)

		then:
		pageSource.contains('created')

		when:
		page.editProfile(un)
		page = at(ProfileEditPage)

		and:
		page.updateProfile(un)

		then:
		at(ProfileListPage)
		pageSource.contains('updated')

		when:
		logout()
		go('')

		then:
		waitFor { pageSource.contains('Log in') }

		when:
		via(ForgotPasswordPage).with {
			username = un
			submit()
		}

		then:
		def securityQuestionPage = at(SecurityQuestionsPage)

		when:
		securityQuestionPage.with {
			question1.text = '1234'
			question2.text = '12345'
			submit()
		}
		page = at(ResetPasswordPage)

		and:
		page.submit()

		then:
		waitFor { pageSource.contains('Password is required') }

		when:
		page.enterNewPassword('abcdefghijk','mnopqrstuwzy')

		then:
		waitFor {
			pageSource.contains('Password must have at least one letter, number, and special character: !@#$%^&')
			pageSource.contains('Passwords do not match')
		}

		when:
		page.enterNewPassword('aaaaaaaa', 'aaaaaaaa')

		then:
		waitFor {
			pageSource.contains('Password must have at least one letter, number, and special character: !@#$%^&')
		}

		when:
		page.enterNewPassword('aaaaaa1#', 'aaaaaa1#')

		then:
		waitFor { pageSource.contains('Your password was successfully changed') }

		when:
		logout()
		go('')

		then:
		waitFor { pageSource.contains('Log in') }

		when:
		page = to(ProfileListPage)

		and:
		page.editProfile(un)
		page = at(ProfileEditPage)

		and:
		page.deleteProfile()

		then:
		waitFor { pageSource.contains('deleted') }

		when:
		go("user/edit?username=$un")
		page = at(UserEditPage)

		then:
		page.username.text == un

		when:
		page.delete()

		then:
		at(UserSearchPage)

		when:
		go("user/edit?username=$un")

		then:
		waitFor { pageSource.contains('User not found') }
	}
}