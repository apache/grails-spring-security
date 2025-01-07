package page.aclClass

import geb.module.TextInput
import page.CreatePage

class AclClassCreatePage extends CreatePage {

	static url = 'aclClass/create'
	static typeName = { 'AclClass' }
	static content = {
		className { $(name: 'className').module(TextInput) }
	}

	void create(String name) {
		className = name
		submit()
	}
}
