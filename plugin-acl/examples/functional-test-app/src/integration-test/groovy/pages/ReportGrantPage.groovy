package pages

class ReportGrantPage extends ScaffoldPage {

	static at = {
		heading.text() ==~ /Grant permission for.+/
	}

	static content = {
		grantButton { $('input', value: 'Grant') }
	}
}
