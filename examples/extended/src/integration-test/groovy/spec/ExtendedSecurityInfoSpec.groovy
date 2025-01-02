package spec

import grails.testing.mixin.integration.Integration
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserCache
import spock.lang.Stepwise

@Stepwise
@Integration
class ExtendedSecurityInfoSpec extends AbstractSecuritySpec {

	UserCache userCache

	void testConfig() {
		when:
		go 'securityInfo/config'

		then:
		assertContentContains 'adh.ajaxErrorPage /login/ajaxDenied'
		assertContentContains 'Showing 1 to 10 of '
	}

	void testMappings() {
		when:
		go 'securityInfo/mappings'

		then:
		assertContentContainsOne 'ROLE_RUN_AS, IS_AUTHENTICATED_FULLY',
		                         'IS_AUTHENTICATED_FULLY, ROLE_RUN_AS'

		assertContentContains '/j_spring_security_switch_user'
		assertContentContains '/secure/**'
		assertContentContains 'ROLE_ADMIN'
	}

	void testCurrentAuth() {
		when:
		go 'securityInfo/currentAuth'

		then:
		assertContentContains 'Details WebAuthenticationDetails'
		assertContentContains '__grails.anonymous.user__'
	}

	void testUsercache() {
		given:
		userCache.putUserInCache(new User('testuser', 'pw', []))

		when:
		go 'securityInfo/usercache'

		then:
		assertContentContains 'UserCache class: org.ehcache.jsr107.Eh107Cache'
		assertContentContains 'testuser'

		cleanup:
		userCache.removeUserFromCache('testuser')
	}

	void testEmptyUsercache() {
		when:
		go 'securityInfo/usercache'

		then:
		assertContentContains 'UserCache class: org.ehcache.jsr107.Eh107Cache'
		assertContentDoesNotContain'testuser'
	}

	void testFilterChains() {
		when:
		go 'securityInfo/filterChains'

		then:
		assertContentContains '/assets/**'
		assertContentContains '/**/js/**'
		assertContentContains '/**/css/**'
		assertContentContains '/**/images/**'
		assertContentContains '/**/favicon.ico'
		assertContentContains '/**'
		assertContentContains 'grails.plugin.springsecurity.web.SecurityRequestHolderFilter'
		assertContentContains 'org.springframework.security.web.access.channel.ChannelProcessingFilter'
		assertContentContains 'org.springframework.security.web.context.SecurityContextPersistenceFilter'
		assertContentContains 'org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter'
		assertContentContains 'grails.plugin.springsecurity.web.filter.GrailsRememberMeAuthenticationFilter'
		assertContentContains 'grails.plugin.springsecurity.web.filter.GrailsAnonymousAuthenticationFilter'

		assertContentContains 'org.springframework.security.web.access.intercept.FilterSecurityInterceptor'
		assertContentContains 'grails.plugin.springsecurity.web.authentication.logout.MutableLogoutFilter'
		assertContentContains 'grails.plugin.springsecurity.web.authentication.GrailsUsernamePasswordAuthenticationFilter'
		assertContentContains 'org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter'
		assertContentContains 'grails.plugin.springsecurity.web.filter.GrailsRememberMeAuthenticationFilter'
		assertContentContains 'grails.plugin.springsecurity.web.filter.GrailsAnonymousAuthenticationFilter'
		//assertContentContains 'org.springframework.security.web.access.ExceptionTranslationFilter'
		assertContentContains 'org.springframework.security.web.access.intercept.FilterSecurityInterceptor'
		assertContentContains 'org.springframework.security.web.authentication.switchuser.SwitchUserFilter'
	}

	void testLogoutHandlers() {
		when:
		go 'securityInfo/logoutHandlers'

		then:
		assertContentContains 'org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices'
		assertContentContains 'org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler'
	}

	void testVoters() {
		when:
		go 'securityInfo/voters'

		then:
		assertContentContains 'org.springframework.security.access.vote.AuthenticatedVoter'
		assertContentContains 'org.springframework.security.access.vote.RoleHierarchyVoter'
		assertContentContains 'grails.plugin.springsecurity.web.access.expression.WebExpressionVoter'
	}

	void testProviders() {
		when:
		go 'securityInfo/providers'

		then:
		assertContentContains 'org.springframework.security.authentication.dao.DaoAuthenticationProvider'
		assertContentContains 'grails.plugin.springsecurity.authentication.GrailsAnonymousAuthenticationProvider'
		assertContentContains 'org.springframework.security.authentication.RememberMeAuthenticationProvider'
	}
}
