package page.aclClass

import geb.module.TextInput
import page.SearchPage

class AclClassSearchPage extends SearchPage {

	static url = 'aclClass/search'
	static typeName = { 'AclClass' }
	static content = {
		className { $(name: 'className').module(TextInput) }
	}

	void search(String q) {
		className = q
		submit()
	}
}
