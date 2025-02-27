package pages

class ShowReportPage extends ScaffoldPage {

	static at = {
		heading.text() == 'Show Report'
	}

	static content = {
		name { $('td#name').text() }
	}
}
