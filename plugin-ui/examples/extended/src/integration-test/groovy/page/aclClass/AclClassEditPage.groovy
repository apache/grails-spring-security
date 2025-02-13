package page.aclClass

import geb.module.TextInput
import page.EditPage

class AclClassEditPage extends EditPage {

	static url = 'aclClass/edit'
	static typeName = { 'AclClass' }
	static content = {
		className { $(name: 'className').module(TextInput) }
	}

	void update(String name) {
		className = name
		submit()
	}
}
