package page.role

import geb.module.TextInput
import page.SearchPage

class RoleSearchPage extends SearchPage {

	static url = 'role/search'
	static typeName = { 'Role' }
	static content = {
		authority { $(name: 'authority').module(TextInput) }
	}

	void search(String q) {
		authority = q
		submit()
	}
}
