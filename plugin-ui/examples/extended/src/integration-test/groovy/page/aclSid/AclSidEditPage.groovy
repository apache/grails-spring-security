package page.aclSid

import geb.module.Checkbox
import geb.module.TextInput
import page.EditPage

class AclSidEditPage extends EditPage {

	static url = 'aclSid/edit'
	static typeName = { 'AclSid' }
	static content = {
		sid { $(name: 'sid').module(TextInput) }
		principal { $(name: 'principal').module(Checkbox) }
	}
}
