package spec

import grails.testing.mixin.integration.Integration
import org.springframework.test.annotation.DirtiesContext
import page.register.ForgotPasswordPage
import page.register.RegisterPage
import page.user.UserEditPage
import page.user.UserSearchPage

import com.dumbster.smtp.SimpleSmtpServer
import com.dumbster.smtp.SmtpMessage
import spock.lang.PendingFeature
import spock.lang.Stepwise

@DirtiesContext(classMode=DirtiesContext.ClassMode.BEFORE_CLASS)
@Stepwise
@Integration
class RegisterSpec extends AbstractSecuritySpec {

	private SimpleSmtpServer server

	void setup() {
		startMailServer()
	}

	void cleanup() {
		server.stop()
	}

	void testRegisterValidation() {
		when:
		to RegisterPage
		submit()

		then:
		assertContentContains 'Username is required'
		assertContentContains 'Email is required'
		assertContentContains 'Password is required'

		when:
		username = 'admin'
		email = 'foo'
		$('#password') << 'abcdefghijk'
		$('#password2') << 'mnopqrstuwzy'
		submit()

		then:
		assertContentContains 'The username is taken'
		assertContentContains 'Please provide a valid email address'
		assertContentContains 'Password must have at least one letter, number, and special character: !@#$%^&'
		assertContentContains 'Passwords do not match'

		when:
		username = 'abcdef123'
		email = 'abcdef@abcdef.com'
		$('#password') << 'aaaaaaaa'
		$('#password2') << 'aaaaaaaa'
		submit()

		then:
		assertContentContains 'Password must have at least one letter, number, and special character: !@#$%^&'
	}

	void testForgotPasswordValidation() {
		when:
		to ForgotPasswordPage
		submit()

		then:
		assertContentContains 'Please enter your username'

		when:
		username = '1111'
		submit()

		then:
		assertContentContains 'No user was found with that username'
	}

	// FIXME
	@DirtiesContext
	@PendingFeature(reason="test is failing at resetPassword stage")
	void testRegisterAndForgotPassword() {

		given:
		String un = 'test_user_abcdef' + System.currentTimeMillis()

		when:
		to RegisterPage

		then:
		at RegisterPage

		when:
		username = un
		email = un + '@abcdef.com'
		$('#password') << 'aaaaaa1#'
		$('#password2') << 'aaaaaa1#'
		submit()

		then:
		assertContentContains 'Your account registration email was sent - check your mail!'
		assert 1 == server.receivedEmailSize

		when:
		def email = currentEmail

		then:
		'New Account' == email.getHeaderValue('Subject')

		when:
		String body = email.body

		then:
		body.contains 'Hi ' + un

		when:
		String code = findCode(body, 'verifyRegistration')
		go 'register/verifyRegistration?t=' + code

		then:
		assertHtmlContains 'Your registration is complete'

		when:
		logout()
		go ''

		then:
		assertContentContains 'Log in'

		when:
		to ForgotPasswordPage
		username = un
		submit()

		then:
		assertContentContains 'Your password reset email was sent - check your mail!'
		2 == server.receivedEmailSize

		when:
		email = currentEmail

		then:
		'Password Reset' == email.getHeaderValue('Subject')

		when:
		body = email.body

		then:
		body.contains('Hi ' + un)

		when:
		code = findCode(body, 'resetPassword')
		go 'register/resetPassword?t=123'

		then:
		assertHtmlContains 'Sorry, we have no record of that request, or it has expired'

		when:
		go 'register/resetPassword?t=' + code

		then:
		assertContentContains 'Reset Password'

		when:
		submit()

		then:
		assertContentContains 'Password is required'

		when:
		go 'register/resetPassword?t=' + code
		$('#password') << 'abcdefghijk'
		$('#password2') << 'mnopqrstuwzy'
		submit()

		then:
		assertContentContains 'Password must have at least one letter, number, and special character: !@#$%^&'
		assertContentContains 'Passwords do not match'

		when:
		go 'register/resetPassword?t=' + code
		$('#password') << 'aaaaaaaa'
		$('#password2') << 'aaaaaaaa'
		submit()

		then:
		assertContentContains 'Password must have at least one letter, number, and special character: !@#$%^&'

		when:
		go 'register/resetPassword?t=' + code
		$('#password') << 'aaaaaa1#'
		$('#password2') << 'aaaaaa1#'
		submit()

		then:
		assertHtmlContains 'Your password was successfully changed'

		when:
		logout()
		go ''

		then:
		assertContentContains 'Log in'

		// delete the user so it doesn't affect other tests
		when:
		go 'user/edit?username=' + un

		then:
		at UserEditPage
		username == un

		when:
		delete()

		then:
		at UserSearchPage

		when:
		go 'user/edit?username=' + un

		then:
		assertHtmlContains 'User not found'
	}

	private SmtpMessage getCurrentEmail() {
		def received = server.receivedEmail
		def email
		while (received.hasNext()) {
			email = received.next()
		}
		email
	}

	private String findCode(String body, String action) {
		def matcher = body =~ /(?s).*$action\?t=(.+)".*/
		assert matcher.hasGroup()
		assert matcher.count == 1
		matcher[0][1]
	}

	private void startMailServer() {

		int port = 1025
		while (true) {
			try {
				new ServerSocket(port).close()
				break
			}
			catch (IOException e) {
				port++
				assert port < 2000, 'cannot find open port'
			}
		}

		server = SimpleSmtpServer.start(port)

		go 'testData/updateMailSenderPort?port=' + port
		assertContentContains 'OK: ' + port
	}
}
