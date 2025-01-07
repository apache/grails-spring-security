package page.profile

import page.AbstractSecurityPage

class ProfileListPage extends AbstractSecurityPage {

	static url = 'profile'
	static at = { title == 'Profile List' }
	static content = {
		profileEditLink { String username -> $('a', text: "User(username:$username)").parent().parent().children().first().children('a')  }
	}

	void editProfile(String username) {
		profileEditLink(username).click()
	}
}
