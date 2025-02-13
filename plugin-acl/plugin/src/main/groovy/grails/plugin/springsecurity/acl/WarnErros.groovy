package grails.plugin.springsecurity.acl

import groovy.transform.CompileDynamic
import org.grails.datastore.gorm.GormValidateable
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

trait WarnErros {

    @CompileDynamic
    String errorsBeanBeingSaved(MessageSource messageSource, GormValidateable bean) {
        StringBuilder message = new StringBuilder("problem creating ${bean.getClass().simpleName}: $bean")
        Locale locale = LocaleContextHolder.getLocale()
        for (fieldErrors in bean.errors) {
            for (error in fieldErrors.allErrors) {
                message << '\n\t' << messageSource.getMessage(error, locale)
            }
        }
        message.toString()
    }
}