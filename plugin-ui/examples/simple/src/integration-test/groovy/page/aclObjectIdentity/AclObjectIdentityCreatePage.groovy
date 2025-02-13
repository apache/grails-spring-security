package page.aclObjectIdentity

import geb.module.Select
import geb.module.TextInput
import page.CreatePage

class AclObjectIdentityCreatePage extends CreatePage {

	static url = 'aclObjectIdentity/create'
	static typeName = { 'AclObjectIdentity' }
	static content = {
		aclClass { $(name: 'aclClass.id').module(Select) }
		objectId { $(name: 'objectId').module(TextInput) }
		ownerId { $(name: 'owner.id').module(Select) }
	}
}
