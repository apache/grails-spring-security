package page

import geb.Page

abstract class AbstractSecurityPage extends Page {

	void submit() {
		submitBtn.click()
	}

	protected boolean assertContentContains(String expected) {
		assert $().text().contains(expected)
		true
	}

	protected boolean assertContentMatches(String regex) {
		assert $().text() ==~ regex
		true
	}

	protected boolean assertContentDoesNotContain(String unexpected) {
		assert !$().text().contains(unexpected)
		true
	}
}
