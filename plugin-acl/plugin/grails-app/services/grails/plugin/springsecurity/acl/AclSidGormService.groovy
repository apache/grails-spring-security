package grails.plugin.springsecurity.acl

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource

@Slf4j
@CompileStatic
class AclSidGormService implements WarnErros {

    MessageSource messageSource

    @ReadOnly
    AclSid findBySidAndPrincipal(String sidName, boolean principal) {
        findQueryBySidAndPrincipal(sidName, principal).get()
    }

    @Transactional
    AclSid saveBySidNameAndPrincipal(String sidName, boolean principal) {
        AclSid aclSidInstance = new AclSid(sid: sidName, principal: principal)
        if ( !aclSidInstance.save() ) {
            log.error '{}', errorsBeanBeingSaved(messageSource, aclSidInstance)
        }
        aclSidInstance
    }

    protected DetachedCriteria<AclSid> findQueryBySidAndPrincipal(String sidName, boolean principalParam) {
        AclSid.where { sid == sidName && principal == principalParam }
    }
}