package page.aclObjectIdentity

import geb.module.Select
import geb.module.TextInput
import page.SearchPage

class AclObjectIdentitySearchPage extends SearchPage {

	static url = 'aclObjectIdentity/search'
	static typeName = { 'AclObjectIdentity' }
	static content = {
		aclClass { $(name: 'aclClass.id').module(Select) }
		objectId { $(name: 'objectId').module(TextInput) }
		ownerId { $(name: 'owner.id').module(Select) }
	}
}
