package grails.plugin.springsecurity.acl.model

import groovy.transform.CompileStatic
import org.springframework.security.acls.model.ObjectIdentityGenerator
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy

/**
 * Convenience interface.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
@CompileStatic
interface ObjectIdentityRetrievalStrategyAndGenerator extends ObjectIdentityRetrievalStrategy, ObjectIdentityGenerator {}
