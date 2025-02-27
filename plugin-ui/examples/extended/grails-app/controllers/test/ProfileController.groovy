package  test

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.validation.ValidationException

class ProfileController extends grails.plugin.springsecurity.ui.AbstractS2UiDomainController {

     ProfileService profileService
    
     static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
     static defaultAction = 'index'

     def search() { redirect action: "index", method: "GET" }

    protected Class<?> getClazz() { Profile }
    protected String getClassLabelCode() { 'profile.label' }
    protected Map model(profile, String action) {
                    [ profile: profile]
    }
    
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        params.offset = Math.max(params.offset ? params.int('offset') : 0, 0)
        def model = [:]
        model.results = profileService.list(params)
        model.totalCount = profileService.count()
        addQueryParamsToModelForPaging(model,
          'myQuestion1','myAnswer1','myQuestion2','myAnswer2',
        'id', 'user.id'
        )
        render view: 'index', model: model
    }
    
    def show(Long id) {
        redirect action: 'edit', id: id
    }
    
    def create() {
        respond new Profile(params), model : ['users': User.list(),'lookupProp':SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName]
    }
    
    def save(Profile profile) {
        withForm {
            if (profile == null) {
                notFound()
                return
            }

            try {
                profileService.save(profile)
            } catch (ValidationException e) {
                respond profile.errors, view:'create', model : ['users': User.list(),'lookupProp':SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName]
                return
            }

            request.withFormat {
                form multipartForm {
                    flashCreated(profile.id)
                    redirect action:"index", method:"GET"
                }
                '*' { respond profile, [status: CREATED] }
            }
        }.invalidToken {
            doSaveWithInvalidTokenSpring Security Management Console()
        }
    }
    
    def edit(Long id) {
        respond profileService.get(id), model : ['users': User.list(),'lookupProp':SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName]
    }
    
    def update(Profile profile) {
        withForm {
            if (profile == null) {
                notFound()
                return
            }

            try {
                profileService.save(profile)
            } catch (ValidationException e) {
                respond profile.errors, view:'edit'
                return
            }

            request.withFormat {
                form multipartForm {
                   flashUpdated()
                   redirect action:"index", method:"GET"
                }
                '*'{ respond profile, [status: OK] }
            }
        }.invalidToken {
             doUpdateWithInvalidToken()
        }
    }
    
    def delete(Long id) {
        withForm {
            if (id == null) {
                notFound()
                return
            }

            profileService.delete(id)

            request.withFormat {
                form multipartForm {
                    flashDeleted()
                    redirect action:"index", method:"GET"
                }
                '*'{ render status: NO_CONTENT }
            }
        }.invalidToken {
            doDeleteWithInvalidToken()
        }
    }
    
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flashNotFound()
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}