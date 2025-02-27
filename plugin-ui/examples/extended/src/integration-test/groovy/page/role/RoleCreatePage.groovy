package page.role

import geb.module.TextInput
import page.CreatePage

class RoleCreatePage extends CreatePage {

	static url = 'role/create'
	static typeName = { 'Role' }
	static content = {
		authority { $(name: 'authority').module(TextInput) }
	}

	void create(String name) {
		authority = name
		submit()
	}
}
