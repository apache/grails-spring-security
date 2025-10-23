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

import grails.testing.mixin.integration.Integration
import page.profile.ProfileCreatePage
import page.profile.ProfileEditPage
import page.profile.ProfileListPage
import page.register.ForgotPasswordPage
import page.register.RegisterPage
import page.register.ResetPasswordPage
import page.register.SecurityQuestionsPage
import page.user.UserEditPage
import page.user.UserSearchPage

@Integration
class RegisterSpec extends AbstractSecuritySpec {

	void testRegisterValidation() {
		when:
		to(RegisterPage).with {
			submit()
		}
		def registerPage = at(RegisterPage)

		then:
		pageSource.contains('Username is required')
		pageSource.contains('Email is required')
		pageSource.contains('Password is required')

		when:
		registerPage.with {
			username = 'admin'
			email = 'foo'
			password = 'abcdefghijk'
			password2 = 'mnopqrstuwzy'
			submit()
		}
		registerPage = at(RegisterPage)

		then:
		pageSource.contains('The username is taken')
		pageSource.contains('Please provide a valid email address')
		pageSource.contains('Password must have at least one letter, number, and special character: !@#$%^&')
		pageSource.contains('Passwords do not match')

		when:
		registerPage.with {
			username = 'abcdef123'
			email = 'abcdef@abcdef.com'
			password = 'aaaaaaaa'
			password2 = 'aaaaaaaa'
			submit()
		}

		then:
		pageSource.contains('Password must have at least one letter, number, and special character: !@#$%^&')
	}

	void testForgotPasswordValidation() {
		when:
		to(ForgotPasswordPage).with {
			submit()
		}
		def forgotPasswordPage = at(ForgotPasswordPage)

		then:
		pageSource.contains('Please enter your username')

		when:
		forgotPasswordPage.with {
			username = '1111'
			submit()
		}

		then:
		at(ForgotPasswordPage)
		pageSource.contains('No user was found with that username')
	}

	void testRegisterAndForgotPassword() {
		given:
		String un = "test_user_abcdef${System.currentTimeMillis()}"

		when:
		go('register/resetPassword?t=123')

		then:
		pageSource.contains('Sorry, we have no record of that request, or it has expired')

		when:
		def registerPage = to(RegisterPage)
		registerPage.with {
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
		def listPage = at(ProfileListPage)

		then:
		pageSource.contains('created')

		when:
		listPage.editProfile(un)
		def profileEditPage = at(ProfileEditPage)

		and:
		profileEditPage.updateProfile(un)

		then:
		at(ProfileListPage)
		pageSource.contains('updated')

		when:
		logout()
		go('')

		then:
		pageSource.contains('Log in')

		when:
		to(ForgotPasswordPage).with {
			username = un
			submit()
		}

		then:
		def securityQuestionPage = at(SecurityQuestionsPage)

		when:
		securityQuestionPage.with {
			question1 = '1234'
			question2 = '12345'
			submit()
		}
		def resetPasswordPage = browser.at(ResetPasswordPage)

		and:
		resetPasswordPage.submit()

		then:
		pageSource.contains('Password is required')

		when:
		resetPasswordPage.enterNewPassword('abcdefghijk','mnopqrstuwzy')

		then:
		pageSource.contains('Password must have at least one letter, number, and special character: !@#$%^&')
		pageSource.contains('Passwords do not match')

		when:
		resetPasswordPage.enterNewPassword('aaaaaaaa', 'aaaaaaaa')

		then:
		pageSource.contains('Password must have at least one letter, number, and special character: !@#$%^&')

		when:
		resetPasswordPage.enterNewPassword('aaaaaa1#', 'aaaaaa1#')

		then:
		waitFor { pageSource.contains('Your password was successfully changed') }

		when:
		logout()
		go('')

		then:
		pageSource.contains('Log in')

		when:
		to(ProfileListPage)

		and:
		listPage.editProfile(un)

		then:
		def profileEditPage2 = browser.at(ProfileEditPage)

		when:
		profileEditPage2.deleteProfile()

		then:
		waitFor { pageSource.contains('deleted') }

		when:
		go("user/edit?username=$un")

		then:
		def userEditPage = at(UserEditPage)
		userEditPage.username.text == un

		when:
		userEditPage.delete()

		then:
		at(UserSearchPage)

		when:
		go("user/edit?username=$un")

		then:
		pageSource.contains('User not found')
	}
}