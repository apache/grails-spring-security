import grails.plugin.geb.ContainerGebSpec
import grails.testing.mixin.integration.Integration
import pages.LoginPage
import pages.LogoutPage

@Integration
abstract class AbstractSecurityFunctionalSpec extends ContainerGebSpec {

	def setup() {
		logout()
	}

	protected void login(String user, String pwd) {
		to LoginPage
		username = user
		password = pwd
		loginButton.click()
	}

	protected void logout() {
		to LogoutPage
		logoutButton.click()
		browser.clearCookies()
		browser.go("/")
	}

	protected void assertContentContains(String expected) {
		assert $().text().contains(expected)
	}

	protected void assertContentDoesNotContain(String unexpected) {
		assert !$().text().contains(unexpected)
	}
}
