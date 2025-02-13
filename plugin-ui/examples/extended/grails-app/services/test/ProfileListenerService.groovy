package  test

import grails.events.annotation.gorm.Listener
import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileStatic
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent

@CompileStatic
class ProfileListenerService {

    SpringSecurityService springSecurityService

    @Listener(Profile)
    void onProfilePreInsert(PreInsertEvent event) {
        
           event.entityAccess.setProperty('myAnswer1' , springSecurityService.encodePassword((event.entityAccess.getProperty('myAnswer1') as String).toLowerCase()))
          
           event.entityAccess.setProperty('myAnswer2' , springSecurityService.encodePassword((event.entityAccess.getProperty('myAnswer2') as String).toLowerCase()))
          
    }

    @Listener(Profile)
    void onProfilePreUpdate(PreUpdateEvent event) {
        Profile prof = ((Profile) event.entityObject)
        
        if ( prof.isDirty('myAnswer1') ) {
            event.entityAccess.setProperty('myAnswer1' , springSecurityService.encodePassword((event.entityAccess.getProperty('myAnswer1') as String).toLowerCase()))
        }
        
        if ( prof.isDirty('myAnswer2') ) {
            event.entityAccess.setProperty('myAnswer2' , springSecurityService.encodePassword((event.entityAccess.getProperty('myAnswer2') as String).toLowerCase()))
        }
        

    }


}
