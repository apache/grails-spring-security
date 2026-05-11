package page.user

import geb.module.Checkbox
import geb.module.TextInput
import module.RolesTab
import page.EditPage
import page.LifecyclePage

class UserEditPage extends EditPage {

    static url = 'user/edit'
    static typeName = { 'User' }
    static content = {
        userId { $('input', type: 'hidden', name: 'id', 0).value() }
        username { $('#username').module(TextInput) }
        enabled { $(name: 'enabled').module(Checkbox) }
        accountExpired { $(name: 'accountExpired').module(Checkbox) }
        accountLocked { $(name: 'accountLocked').module(Checkbox) }
        passwordExpired { $(name: 'passwordExpired').module(Checkbox) }
        rolesTab { module(RolesTab) }
    }

    def <T extends LifecyclePage> T submitEdit(UserForm formData = null, Class<T> expectedPageType) {
        formData?.applyTo(this)
        super.submitEdit(expectedPageType)
    }
}
