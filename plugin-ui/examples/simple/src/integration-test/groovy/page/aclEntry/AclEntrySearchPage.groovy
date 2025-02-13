package page.aclEntry

import geb.module.TextInput
import page.SearchPage

class AclEntrySearchPage extends SearchPage {

	static url = 'aclEntry/search'
	static typeName = { 'AclEntry' }
	static content = {
		aclObjectIdentity { $(name: 'aclObjectIdentity.id').module(TextInput) }
		aceOrder { $(name: 'aceOrder').module(TextInput) }
		mask { $(name: 'mask').module(TextInput) }
	}
}
