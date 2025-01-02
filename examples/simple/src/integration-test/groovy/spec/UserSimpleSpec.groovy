package spec

import grails.testing.mixin.integration.Integration
import page.user.UserCreatePage
import page.user.UserEditPage
import page.user.UserSearchPage
import spock.lang.Issue
import spock.lang.Stepwise

@Stepwise
@Integration
class UserSimpleSpec extends AbstractSecuritySpec {

    void testFindAll() {
        when:
        to UserSearchPage

        then:
        assertNotSearched()

        when:
        submit()

        then:
        at UserSearchPage
        assertResults 1, 10, 22
    }

    void testFindByUsername() {
        when:
        to UserSearchPage

        username = 'foo'
        submit()

        then:
        at UserSearchPage
        assertResults 1, 3, 3

        assertContentContains 'foon_2'
        assertContentContains 'foolkiller'
        assertContentContains 'foostra'
    }

    void testFindByDisabled() {
        when:
        to UserSearchPage

        enabled.checked = '-1'
        submit()

        then:
        at UserSearchPage
        assertResults 1, 1, 1
        assertContentContains 'billy9494'
    }

    void testFindByAccountExpired() {
        when:
        to UserSearchPage

        accountExpired.checked = '1'

        submit()

        then:
        at UserSearchPage
        assertResults 1, 3, 3
        assertContentContains 'maryrose'
        assertContentContains 'ratuig'
        assertContentContains 'rome20c'
    }

    void testFindByAccountLocked() {
        when:
        to UserSearchPage

        accountLocked.checked = '1'

        submit()

        then:
        at UserSearchPage
        assertResults 1, 3, 3
        assertContentContains 'aaaaaasd'
        assertContentContains 'achen'
        assertContentContains 'szhang1999'
    }

    void testFindByPasswordExpired() {
        when:
        to UserSearchPage

        passwordExpired.checked = '1'

        submit()

        then:
        at UserSearchPage
        assertResults 1, 3, 3
        assertContentContains 'hhheeeaaatt'
        assertContentContains 'mscanio'
        assertContentContains 'kittal'
    }

    void testCreateAndEdit() {
        given:
        String newUsername = 'newuser' + UUID.randomUUID()

        // make sure it doesn't exist
        when:
        to UserSearchPage

        username = newUsername
        submit()

        then:
        at UserSearchPage
        assertNoResults()

        // create
        when:
        to UserCreatePage

        username = newUsername
        $('#password') << 'password'
        enabled.check()
        submit()

        then:
        at UserEditPage
        username == newUsername
        enabled.checked
        !accountExpired.checked
        !accountLocked.checked
        !passwordExpired.checked

        // edit
        when:
        String updatedName = newUsername + '_updated'
        username = updatedName
        enabled.uncheck()
        accountExpired.check()
        accountLocked.check()
        passwordExpired.check()
        submit()

        then:
        at UserEditPage
        username == updatedName
        !enabled.checked
        accountExpired.checked
        accountLocked.checked
        passwordExpired.checked

        // delete
        when:
        delete()

        then:
        at UserSearchPage

        when:
        username = updatedName
        submit()

        then:
        at UserSearchPage
        assertNoResults()
    }

    @Issue("https://github.com/grails-plugins/grails-spring-security-ui/issues/89")
    void testUserRoleAssociationsAreNotRemoved() {
        when: "edit user 1"
        go 'user/edit/1'

        then:
        at UserEditPage

        when: "select Roles tab"
        rolesTab.select()

        then: "12 roles are listed and 1 is enabled"
        assert rolesTab.totalRoles() == 12
        assert rolesTab.totalEnabledRoles() == 1
        assert rolesTab.hasEnabledRole('ROLE_USER')

        when: "ROLE_ADMIN is enabled and the changes are saved"
        rolesTab.enableRole "ROLE_ADMIN"
        submit()
        rolesTab.select()

        then: "12 roles are listed and 2 are enabled"
        assert rolesTab.totalEnabledRoles() == 2
        assert rolesTab.hasEnabledRoles(['ROLE_USER', 'ROLE_ADMIN'])
        assert rolesTab.totalRoles() == 12
    }

    @Issue("https://github.com/grails-plugins/grails-spring-security-ui/issues/106")
    void testUserRoleAssociationsAreRemoved() {
        when: "edit user 2"
        go 'user/edit/2'

        then:
        at UserEditPage

        when: "select Roles tab"
        rolesTab.select()

        then: "12 roles are listed and 1 is enabled"
        assert rolesTab.totalRoles() == 12
        assert rolesTab.totalEnabledRoles() == 1
        assert rolesTab.hasEnabledRole('ROLE_USER')

        when: "ROLE_ADMIN is enabled and the changes are saved"
        rolesTab.enableRole "ROLE_ADMIN"
        submit()
        rolesTab.select()

        then: "12 roles are listed and 2 are enabled"
        assert rolesTab.totalEnabledRoles() == 2
        assert rolesTab.hasEnabledRoles(['ROLE_USER', 'ROLE_ADMIN'])
        assert rolesTab.totalRoles() == 12

        when: "edit user 2"
        go 'user/edit/2'

        then:
        at UserEditPage

        when: "select Roles tab"
        rolesTab.select()

        then: "12 roles are listed and 2 are enabled"
        assert rolesTab.totalRoles() == 12
        assert rolesTab.totalEnabledRoles() == 2
        assert rolesTab.hasEnabledRole('ROLE_USER')

        when: "ROLE_ADMIN is disabled and the changes are saved"
        rolesTab.disableRole "ROLE_ADMIN"
        submit()
        go 'user/edit/2'

        then:
        at UserEditPage

        when: "select Roles tab"
        rolesTab.select()

        then: "12 roles are listed and 1 is enabled"
        assert rolesTab.totalEnabledRoles() == 1
        assert rolesTab.hasEnabledRoles(['ROLE_USER'])
        assert rolesTab.totalRoles() == 12
    }
}
