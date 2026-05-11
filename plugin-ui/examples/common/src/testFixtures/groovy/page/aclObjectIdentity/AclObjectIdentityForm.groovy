package page.aclObjectIdentity

import groovy.transform.Immutable

import geb.Page
import geb.module.Select
import geb.module.TextInput

@Immutable
class AclObjectIdentityForm {

    String aclClass
    String objectId
    String ownerId

    <P extends Page> void applyTo(P page) {
        if (aclClass != null) page.$(name: 'aclClass.id').module(Select).selected = aclClass
        if (objectId != null) page.$(name: 'objectId').module(TextInput).text = objectId
        if (ownerId != null) page.$(name: 'owner.id').module(Select).selected = ownerId
    }
}
