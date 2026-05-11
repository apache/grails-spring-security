package page.role

import groovy.transform.Immutable

import geb.module.TextInput
import page.LifecyclePage

@Immutable
class RoleForm {

    String authority

     <P extends LifecyclePage> void applyTo(P page) {
        if (authority != null) page.$(name: 'authority').module(TextInput).text = authority
     }
}
