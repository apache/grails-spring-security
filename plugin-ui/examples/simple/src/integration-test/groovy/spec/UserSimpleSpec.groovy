/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package spec

import grails.testing.mixin.integration.Integration
import page.user.UserCreatePage
import page.user.UserEditPage
import page.user.UserSearchPage
import spock.lang.Issue

@Integration
class UserSimpleSpec extends AbstractSecuritySpec {

    void testFindAll() {
        when:
        def searchPage = to(UserSearchPage)

        then:
        searchPage.assertNotSearched()

        when:
        searchPage.submit()
        searchPage = at(UserSearchPage)

        then:
        searchPage.assertResults(1, 10, 22)
    }

    void testFindByUsername() {
        when:
        to(UserSearchPage).tap {
            username = 'foo'
            submit()
        }
        def searchPage = at(UserSearchPage)

        then:
        searchPage.assertResults(1, 3, 3)
        pageSource.contains('foon_2')
        pageSource.contains('foolkiller')
        pageSource.contains('foostra')
    }

    void testFindByDisabled() {
        when:
        def searchPage = to(UserSearchPage)

        // Temporary workaround for problem with Geb RadioButtons module
        //searchPage.enabled.checked = '-1'
        $('input', type: 'radio', name: 'enabled', value: '-1').click()
        searchPage.submit()
        searchPage = at(UserSearchPage)

        then:
        searchPage.assertResults(1, 1, 1)
        pageSource.contains('billy9494')
    }

    void testFindByAccountExpired() {
        when:
        def searchPage = to(UserSearchPage)

        // Temporary workaround for problem with Geb RadioButtons module
        //searchPage.accountExpired.checked = '1'
        $('input', type: 'radio', name: 'accountExpired', value: '1').click()
        searchPage.submit()
        searchPage = at(UserSearchPage)

        then:
        searchPage.assertResults(1, 3, 3)
        pageSource.contains('maryrose')
        pageSource.contains('ratuig')
        pageSource.contains('rome20c')
    }

    void testFindByAccountLocked() {
        when:
        def searchPage = to(UserSearchPage)

        // Temporary workaround for problem with Geb RadioButtons module
        //searchPage.accountLocked.checked = '1'
        $('input', type: 'radio', name: 'accountLocked', value: '1').click()
        searchPage.submit()
        searchPage = at(UserSearchPage)

        then:
        searchPage.assertResults(1, 3, 3)
        pageSource.contains('aaaaaasd')
        pageSource.contains('achen')
        pageSource.contains('szhang1999')
    }

    void testFindByPasswordExpired() {
        when:
        def searchPage = to(UserSearchPage)

        // Temporary workaround for problem with Geb RadioButtons module
        //searchPage.passwordExpired.checked = '1'
        $('input', type: 'radio', name: 'passwordExpired', value: '1').click()
        searchPage.submit()
        searchPage = at(UserSearchPage)

        then:
        searchPage.assertResults(1, 3, 3)
        pageSource.contains('hhheeeaaatt')
        pageSource.contains('mscanio')
        pageSource.contains('kittal')
    }

    void testCreateAndEdit() {
        given:
        String newUsername = "newuser${UUID.randomUUID()}"

        // make sure it doesn't exist
        when:
        to(UserSearchPage).with {
            username = newUsername
            submit()
        }
        def searchPage = at(UserSearchPage)

        then:
        searchPage.assertNoResults()

        // create
        when:
        to(UserCreatePage).with {
            username = newUsername
            password =  'password'
            enabled.check()
            submit()
        }
        def editPage = at(UserEditPage)

        then:
        editPage.username.text == newUsername
        editPage.enabled.checked
        !editPage.accountExpired.checked
        !editPage.accountLocked.checked
        !editPage.passwordExpired.checked

        // edit
        when:
        String updatedName = "${newUsername}_updated"
        editPage.with {
            username = updatedName
            enabled.uncheck()
            accountExpired.check()
            accountLocked.check()
            passwordExpired.check()
            submit()
        }
        editPage = at(UserEditPage)

        then:
        editPage.username.text == updatedName
        !editPage.enabled.checked
        editPage.accountExpired.checked
        editPage.accountLocked.checked
        editPage.passwordExpired.checked

        // delete
        when:
        editPage.delete()
        searchPage = at(UserSearchPage)

        and:
        searchPage.with {
            username = updatedName
            submit()
        }
        searchPage = at(UserSearchPage)

        then:
        searchPage.assertNoResults()
    }

    @Issue('https://github.com/grails-plugins/grails-spring-security-ui/issues/89')
    void testUserRoleAssociationsAreNotRemoved() {
        when: 'edit user 1'
        go('user/edit/1')
        def editPage = at(UserEditPage)

        and: 'select Roles tab'
        editPage.rolesTab.select()

        then: '12 roles are listed and 1 is enabled'
        editPage.rolesTab.totalRoles() == 12
        editPage.rolesTab.totalEnabledRoles() == 1
        editPage.rolesTab.hasEnabledRole('ROLE_USER')

        when: 'ROLE_ADMIN is enabled and the changes are saved'
        editPage.with {
            rolesTab.enableRole 'ROLE_ADMIN'
            submit()
            rolesTab.select()
        }

        then: '12 roles are listed and 2 are enabled'
        editPage.rolesTab.totalEnabledRoles() == 2
        editPage.rolesTab.hasEnabledRoles(['ROLE_USER', 'ROLE_ADMIN'])
        editPage.rolesTab.totalRoles() == 12
    }

    @Issue('https://github.com/grails-plugins/grails-spring-security-ui/issues/106')
    void testUserRoleAssociationsAreRemoved() {
        when: 'edit user 2'
        go('user/edit/2')
        def editPage = at(UserEditPage)

        and: 'select Roles tab'
        editPage.rolesTab.select()

        then: '12 roles are listed and 1 is enabled'
        editPage.rolesTab.totalRoles() == 12
        editPage.rolesTab.totalEnabledRoles() == 1
        editPage.rolesTab.hasEnabledRole('ROLE_USER')

        when: 'ROLE_ADMIN is enabled and the changes are saved'
        editPage.with {
            rolesTab.enableRole('ROLE_ADMIN')
            submit()
            rolesTab.select()
        }

        then: '12 roles are listed and 2 are enabled'
        editPage.rolesTab.totalEnabledRoles() == 2
        editPage.rolesTab.hasEnabledRoles(['ROLE_USER', 'ROLE_ADMIN'])
        editPage.rolesTab.totalRoles() == 12

        when: 'edit user 2'
        go('user/edit/2')
        editPage = at(UserEditPage)

        and: 'select Roles tab'
        editPage.rolesTab.select()

        then: '12 roles are listed and 2 are enabled'
        editPage.rolesTab.totalRoles() == 12
        editPage.rolesTab.totalEnabledRoles() == 2
        editPage.rolesTab.hasEnabledRole('ROLE_USER')

        when: 'ROLE_ADMIN is disabled and the changes are saved'
        editPage.with {
            rolesTab.disableRole('ROLE_ADMIN')
            submit()
        }
        go('user/edit/2')
        editPage = at(UserEditPage)

        and: 'select Roles tab'
        editPage.rolesTab.select()

        then: '12 roles are listed and 1 is enabled'
        editPage.rolesTab.totalEnabledRoles() == 1
        editPage.rolesTab.hasEnabledRoles(['ROLE_USER'])
        editPage.rolesTab.totalRoles() == 12
    }
}
