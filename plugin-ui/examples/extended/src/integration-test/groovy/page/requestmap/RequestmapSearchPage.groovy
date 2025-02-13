package page.requestmap

import geb.module.TextInput
import page.SearchPage

class RequestmapSearchPage extends SearchPage {

	static url = 'requestmap/search'
	static typeName = { 'Requestmap' }
	static content = {
		configAttribute { $(name: 'configAttribute').module(TextInput) }
		urlPattern { $(name: 'url').module(TextInput) }
	}
}
