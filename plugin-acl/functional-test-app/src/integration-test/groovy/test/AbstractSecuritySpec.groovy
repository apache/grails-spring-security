package test

import geb.spock.GebReportingSpec
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import pages.LoginPage
import spock.lang.Shared

@Integration
@Rollback
abstract class AbstractSecuritySpec extends GebReportingSpec {

	@Shared boolean reset = false

	void setup() {
		browser.baseUrl = "http://localhost:${serverPort}/"
		if ( !reset ) {
			go 'testData/reset'
			reset = true
		}
		logout()
	}

	protected void login(String user) {
		to LoginPage
		username = user
		password = 'password'
		loginButton.click()
	}

	protected void logout() {
		go 'logout'
		browser.clearCookies()
	}

	protected boolean contentContains(String expected) {
		browser.driver.pageSource.contains(expected)
	}

	protected void assertContentContains(String expected) {
		assert contentContains(expected)
	}

	protected void assertContentDoesNotContain(String unexpected) {
		assert !contentContains(unexpected)
	}
}
