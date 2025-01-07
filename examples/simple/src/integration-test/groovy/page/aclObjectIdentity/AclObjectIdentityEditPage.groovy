package page.aclObjectIdentity

import geb.module.TextInput
import page.EditPage

class AclObjectIdentityEditPage extends EditPage {

	static url = 'aclObjectIdentity/edit'
	static typeName = { 'AclObjectIdentity' }
	static content = {
		objectId { $(name: 'objectId').module(TextInput) }
	}
}
