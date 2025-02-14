package pages

class EditReportPage extends ScaffoldPage {

	static at = {
		heading.text() == 'Edit Report'
	}

	static content = {
		updateButton { $('input', value: 'Update') }
	}
}
