package page.aclEntry

import groovy.transform.Immutable

import geb.Page
import geb.module.Checkbox
import geb.module.Select
import geb.module.TextInput

@Immutable
class AclEntryForm {

    String aclObjectIdentityId
    String aceOrder
    String mask
    String sid
    Boolean auditFailure
    Boolean auditSuccess
    Boolean granting

    void applyTo(Page page) {
        if (aclObjectIdentityId != null) {
            page.$(name: 'aclObjectIdentity.id').module(TextInput).text = aclObjectIdentityId
        }
        if (aceOrder != null) {
            page.$(name: 'aceOrder').module(TextInput).text = aceOrder
        }
        if (mask != null) {
            page.$(name: 'mask').module(TextInput).text = mask
        }
        if (sid != null) {
            page.$(name: 'sid.id').module(Select).selected = sid
        }
        if (auditFailure != null) {
            applyToCheckbox(page.$(name: 'auditFailure').module(Checkbox), auditFailure)
        }
        if (auditSuccess != null) {
            applyToCheckbox(page.$(name: 'auditSuccess').module(Checkbox), auditSuccess)
        }
        if (granting != null) {
            applyToCheckbox(page.$(name: 'granting').module(Checkbox), granting)
        }
    }

    private static void applyToCheckbox(Checkbox checkbox, boolean value) {
        if (value) checkbox.check() else checkbox.uncheck()
    }
}
