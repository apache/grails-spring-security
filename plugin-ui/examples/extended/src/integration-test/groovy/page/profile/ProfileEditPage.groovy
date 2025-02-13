package page.profile

import geb.module.Select
import geb.module.TextInput
import page.AbstractSecurityPage

class ProfileEditPage extends AbstractSecurityPage {

	static url = 'profile/edit'
	static at = { title == 'Edit Profile' }
	static content = {
		myQuestion { $('#myQuestion1').module(TextInput) }
		myQuestion2 { $('#myQuestion2').module(TextInput) }
		myAnswer2 { $('#myAnswer2').module(TextInput) }
		myAnswer { $('#myAnswer1').module(TextInput) }
		submitBtn { $('#update')}
	}

	void updateProfile(String userName) {
		def userSelect = $(name: 'user.id').module(Select)
		userSelect.selected  = userName
		myQuestion = 'Count to 4'
		myQuestion2 = 'Count to 5'
		myAnswer2  = '12345'
		myAnswer = '1234'
		submit()
	}

	void deleteProfile() {
		$('#deleteButton').click()
		waitFor {
			$('span', text: 'Are you sure?')
		}
		$('button', text: 'Delete').click()
	}
}