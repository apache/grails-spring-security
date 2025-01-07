package page.aclSid

import geb.module.Checkbox
import geb.module.TextInput
import page.CreatePage

class AclSidCreatePage extends CreatePage {

	static url = 'aclSid/create'
	static typeName = { 'AclSid' }
	static content = {
		sid { $(name: 'sid').module(TextInput) }
		principal { $(name: 'principal').module(Checkbox) }
	}

	void create(String name, boolean isPrincipal) {
		sid = name
		if (isPrincipal) {
			principal.check()
		}
		submit()
	}
}
