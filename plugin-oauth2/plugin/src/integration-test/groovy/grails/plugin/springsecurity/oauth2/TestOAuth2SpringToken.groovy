package grails.plugin.springsecurity.oauth2

import com.github.scribejava.core.model.OAuth2AccessToken
import grails.plugin.springsecurity.oauth2.token.OAuth2SpringToken
import grails.plugin.springsecurity.userdetails.GrailsUser

/**
 * A basic implementation for oauth token for a loggedin user.
 */
class TestOAuth2SpringToken extends OAuth2SpringToken {

	TestOAuth2SpringToken(OAuth2AccessToken token, boolean json) {
		super(token, json)
		this.principal = new GrailsUser("username", "password", true, true, true, true, [], 1L)
	}

	String getProviderName() {
		return "provider"
	}

	String getSocialId() {
		return "socialId"
	}

	String getScreenName() {
		return "screenName"
	}
}