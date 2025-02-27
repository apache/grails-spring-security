package module

import geb.Module
import geb.navigator.Navigator

/**
 * Module representing the Roles tab which is displayed on the User edit page
 */
class RolesTab extends Module {

    static content = {
        tab { $('a', href: '#tab-roles') }
    }

    void select() {
        tab.click()
        waitFor { $('li', 'aria-controls': 'tab-roles', 'aria-selected': 'true') }
    }

    int totalRoles() {
        return $('input', type: 'checkbox', id: startsWith('ROLE_')).size().toInteger()
    }

    int totalEnabledRoles() {
        return findAllEnabledRoles().size().toInteger()
    }

    Navigator findAllEnabledRoles() {
        return $('input', type: 'checkbox', id: startsWith('ROLE_'), checked: 'checked')
    }

    void enableRole(String roleName) {
        $('input', type: 'checkbox', id: roleName).value(true)
    }

    void disableRole(String roleName) {
        $('input', type: 'checkbox', id: roleName).value(false)
    }

    boolean hasEnabledRole(String roleName) {
        return hasEnabledRoles([roleName])
    }

    boolean hasEnabledRoles(List<String> roleNames) {
        return findAllEnabledRoles().collect { it.attr('id') }.containsAll(roleNames)
    }
}
