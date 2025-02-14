package pages

import geb.Module

class ListReportPage extends ScaffoldPage {

	static url = 'report/list?max=1000'

	static at = {
		title ==~ /Report List/
	}

	static content = {
		reportTable { $('div.list table', 0) }
		reportRow { i -> module ReportRow, reportRows[i] }
		reportRows(required: false) { reportTable.find('tbody').find('tr') }
	}
}

class ReportRow extends Module {
	static content = {
		cell { i -> $('td', i) }
		cellText { i -> cell(i).text() }
		cellHrefText{ i -> cell(i).find('a').text() }
		name { cellText(1) }
		showLink(to: ShowReportPage) { cell(0).find('a') }
		grantLink(to: ReportGrantPage) { cell(2).find('a') }
	}
}
