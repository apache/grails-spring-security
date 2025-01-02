package test

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import grails.testing.services.ServiceUnitTest
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification
import org.hibernate.SessionFactory
import spock.lang.Stepwise

@Stepwise
@Integration
@Rollback
class ProfileServiceSpec extends Specification {

    ProfileService profileService
    SessionFactory sessionFactory

    private Long setupData() {
    }

    void "test get"() {
        setupData()

        expect:
        profileService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Profile> profileList = profileService.list(max: 2, offset: 2)

        then:
        profileList.size() == 2

    }

    void "test count"() {
        setupData()

        expect:
        profileService.count() == 4
    }

    void "test delete"() {

        expect:
        profileService.count() == 4

        when:
        profileService.delete(Profile.first().id)
        sessionFactory.currentSession.flush()

        then:
        profileService.count() == 3
    }

    void "test save"() {
        when:

        Profile profile = new Profile(user:User.findByUsername("foon_2"),myAnswer1:'1234',myQuestion1: "Count to four",myAnswer2: '12345', myQuestion2: 'Count to Five')
        profileService.save(profile)

        then:
        profile.id != null
    }
}
