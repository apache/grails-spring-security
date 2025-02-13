package grails.plugin.springsecurity.acl

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import groovy.transform.CompileStatic

@CompileStatic
class AclEntryGormService {

    @ReadOnly
    List<AclEntry> findAllByAclObjectIdentity(AclObjectIdentity aclObjectIdentity) {
        findQueryByAclObjectIdentity(aclObjectIdentity).list()
    }

    @ReadOnly
    List<Serializable> findAllIdByAclObjectIdentity(AclObjectIdentity oid) {
        findQueryByAclObjectIdentity(oid).id().list() as List<Serializable>
    }

    protected DetachedCriteria<AclEntry> findQueryByAclObjectIdentity(AclObjectIdentity aclObjectIdentityParam) {
        AclEntry.where { aclObjectIdentity == aclObjectIdentityParam }
    }

}