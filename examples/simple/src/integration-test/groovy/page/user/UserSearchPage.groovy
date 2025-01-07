package page.user

import geb.module.RadioButtons
import geb.module.TextInput
import page.SearchPage

class UserSearchPage extends SearchPage {

	static url = 'user/search'
	static typeName = { 'User' }
	static content = {
		username { $('#username').module(TextInput) }
		enabled { $(name: 'enabled').module(RadioButtons) }
		accountExpired { $(name: 'accountExpired').module(RadioButtons) }
		accountLocked { $(name: 'accountLocked').module(RadioButtons) }
		passwordExpired { $(name: 'passwordExpired').module(RadioButtons) }
	}
}
