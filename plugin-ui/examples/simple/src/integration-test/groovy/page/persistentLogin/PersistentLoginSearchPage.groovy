package page.persistentLogin

import geb.module.TextInput
import page.SearchPage

class PersistentLoginSearchPage extends SearchPage {

	static url = 'persistentLogin/search'
	static typeName = { 'PersistentLogin' }
	static content = {
		series { $(name: 'series').module(TextInput) }
		token { $(name: 'token').module(TextInput) }
		username { $('#username').module(TextInput) }
	}
}
