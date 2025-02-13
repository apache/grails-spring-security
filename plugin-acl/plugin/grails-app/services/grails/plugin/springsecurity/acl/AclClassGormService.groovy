package grails.plugin.springsecurity.acl

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import groovy.transform.CompileStatic

@CompileStatic
class AclClassGormService {

    @ReadOnly
    AclClass findByClassName(String className) {
        findQueryByClassName(className).get()
    }

    protected DetachedCriteria<AclClass> findQueryByClassName(String classNameParam ) {
        AclClass.where { className == classNameParam }
    }
}