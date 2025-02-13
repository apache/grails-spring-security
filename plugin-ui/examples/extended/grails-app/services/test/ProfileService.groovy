package  test

import grails.gorm.services.Service

@Service(Profile)
interface ProfileService {

    Profile get(Serializable id)

    List<Profile> list(Map args)

    Long count()

    void delete(Serializable id)

    Profile save(Profile profile)

}