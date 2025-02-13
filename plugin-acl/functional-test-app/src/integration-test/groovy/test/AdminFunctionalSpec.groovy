package test

import org.springframework.security.acls.domain.BasePermission

import pages.EditReportPage
import pages.IndexPage
import pages.ListReportPage
import pages.ReportGrantPage
import pages.ShowReportPage
import spock.lang.Stepwise
import spock.lang.IgnoreIf

@IgnoreIf( { !System.getProperty('geb.env') })
@Stepwise
class AdminFunctionalSpec extends AbstractSecuritySpec {

	// admin has admin on all

	void setup() {
		login 'admin'
	}

	void 'check tags'() {
		when:
		go 'tagLibTest/test'

		then:
		assertContentContains 'test 1 true 1'
		assertContentContains 'test 2 true 1'
		assertContentContains 'test 3 true 1'
		assertContentContains 'test 4 true 1'
		assertContentContains 'test 5 true 1'
		assertContentContains 'test 6 true 1'

		assertContentContains 'test 1 true 13'
		assertContentContains 'test 2 true 13'
		assertContentContains 'test 3 true 13'
		assertContentContains 'test 4 true 13'
		assertContentContains 'test 5 true 13'
		assertContentContains 'test 6 true 13'

		assertContentContains 'test 1 true 80'
		assertContentContains 'test 2 true 80'
		assertContentContains 'test 3 true 80'
		assertContentContains 'test 4 true 80'
		assertContentContains 'test 5 true 80'
		assertContentContains 'test 6 true 80'
	}

	void 'view all'() {
		when:
		go "report/show?number=$i"

		then:
		assertContentContains "report$i"

		where:
		i << (1..100)
	}

	void 'edit report 15'() {

		when:
		go 'report/edit?number=15'

		then:
		at EditReportPage
		$('form').name == 'report15'

		when:
		name = 'report15_new'
		updateButton.click()

		then:
		at ShowReportPage
		assertContentContains 'report15_new'
	}

	void 'delete report 15'() {
		when:
		go 'report/delete?number=15'

		then:
		at ListReportPage
		message == 'Report 15 deleted'
		reportRows.size() == 99
	}

	void 'grant edit 16'() {
		when:
		go 'report/grant?number=16'

		then:
		at ReportGrantPage
		assertContentContains 'Grant permission for report16'

		when:
		recipient = 'user2'
		permission = BasePermission.READ.mask.toString()
		grantButton.click()

		then:
		at ShowReportPage
		assertContentContains "Permission $BasePermission.READ.mask granted on Report 16 to user2"

		// login as user2 and verify the grant
		when:
		go 'logout'

		then:
		at IndexPage

		when:
		login 'user2'

		then:
		at IndexPage

		when:
		go 'report/show?number=16'

		then:
		assertContentContains 'report16'
	}
}
