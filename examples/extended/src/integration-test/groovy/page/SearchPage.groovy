package page

abstract class SearchPage extends AbstractSecurityPage {

	static at = { title == "${typeName()} Search" }
	static atCheckWaiting = true
	static content = {
		form { $('search') }
		submitBtn { $('a', id: 'searchButton') }
	}

	boolean assertNoResults() {
		assertContentContains('No results')
		assertContentDoesNotContain('Showing')
		true
	}

	boolean assertNotSearched() {
		assertContentContains('Search')
		assertContentDoesNotContain('No results')
		assertContentDoesNotContain('Showing')
		true
	}

	boolean assertResults(int start, int end, int total) {
		assertContentContains("Showing $start through $end out of ${total}.")
		true
	}
}
