package page.role

import geb.module.TextInput
import page.EditPage

class RoleEditPage extends EditPage {

	static url = 'role/edit'
	static typeName = { 'Role' }
	static content = {
		authority { $(name: 'authority').module(TextInput) }
	}

	void update(String name) {
		authority = name
		submit()
	}
}
