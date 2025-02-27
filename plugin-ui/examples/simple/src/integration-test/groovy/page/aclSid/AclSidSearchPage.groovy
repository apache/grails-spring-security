package page.aclSid

import geb.module.RadioButtons
import geb.module.TextInput
import page.SearchPage

class AclSidSearchPage extends SearchPage {

	static url = 'aclSid/search'
	static typeName = { 'AclSid' }
	static content = {
		sid { $(name: 'sid').module(TextInput) }
		principal { $(name: 'principal').module(RadioButtons) }
	}

	void search(String q) {
		sid = 'user'
		submit()
	}
}
