package page.aclEntry

import geb.module.Checkbox
import geb.module.Select
import geb.module.TextInput
import page.CreatePage

class AclEntryCreatePage extends CreatePage {

	static url = 'aclEntry/create'
	static typeName = { 'AclEntry' }
	static content = {
		aclObjectIdentityId { $(name: 'aclObjectIdentity.id').module(TextInput) }
		aceOrder { $(name: 'aceOrder').module(TextInput) }
		mask { $(name: 'mask').module(TextInput) }
		sid { $(name: 'sid.id').module(Select) }
		auditFailure { $(name: 'auditFailure').module(Checkbox) }
		auditSuccess { $(name: 'auditSuccess').module(Checkbox) }
		granting { $(name: 'granting').module(Checkbox) }
	}
}
