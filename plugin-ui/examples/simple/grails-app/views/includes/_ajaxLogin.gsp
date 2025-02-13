<g:set var='securityConfig' value='${applicationContext.springSecurityService.securityConfig}'/>
					<style>
					input.login {
						display: block;
						width: 400px;
					}
					</style>
					<span id="s2ui_login_link_container">
						<nobr>
						<span id="logoutLink" style="display:none">
						<g:link elementId='_logout' controller='logout'/>
						<a href="${request.contextPath}${securityConfig.logout.afterLogoutUrl}" id="_afterLogout"></a>
						</span>
						<div id="loginLinkContainer">
							<sec:ifLoggedIn>
							<g:message code='spring.security.ui.login.loggedInAs' args='[sec.username()]'/>
								(<g:link controller='logout' elementId='logout'><g:message code='spring.security.ui.login.logout'/></g:link>)
							</sec:ifLoggedIn>
							<sec:ifNotLoggedIn>
							<a href="#" id="loginLink"><g:message code='spring.security.ui.login.login'/></a>
							</sec:ifNotLoggedIn>
							<sec:ifSwitched>
							<a href="${request.contextPath}${securityConfig.switchUser.exitUserUrl}">
								<g:message code='spring.security.ui.login.resumeAs' args='[sec.switchedUserOriginalUsername()]'/>
							</a>
							</sec:ifSwitched>
						</div>
						</nobr>
					</span>
					<div class="s2ui_center">
						<div id="loginFormContainer" style="display:none" title="${message(code:'spring.security.ui.login.signin')}">
							<form action="${request.contextPath}${securityConfig.apf.filterProcessesUrl}" method="post" id="loginForm" name="loginForm" autocomplete="off">
							<label for="ajaxUsername"><g:message code='spring.security.ui.login.username'/></label>
							<input class="login" name="${securityConfig.apf.usernameParameter}" id="ajaxUsername" size="20"/>
							<label for="ajaxPassword"><g:message code='spring.security.ui.login.password'/></label>
							<input class="login" type="password" name="${securityConfig.apf.passwordParameter}" id="ajaxPassword" size="20"/>
							<input type="checkbox" class="checkbox" name="${securityConfig.rememberMe.parameter}" id="remember_me" checked="checked"/>
							<label for="remember_me"><g:message code='spring.security.ui.login.rememberme'/></label> |
							<g:link controller='register' action='forgotPassword'><g:message code='spring.security.ui.login.forgotPassword'/></g:link>
							<g:link controller='register'><g:message code='spring.security.ui.login.register'/></g:link>
							<input type="submit" class="s2ui_hidden_button"/>
							</form>
							<div id="loginMessage" style="color: red; margin-top: 10px;"></div>
						</div>
					</div>
<asset:script>
var loginButtonCaption = "<g:message code='spring.security.ui.login.login'/>";
var cancelButtonCaption = "<g:message code='spring.security.ui.login.cancel'/>";
var loggingYouIn = "<g:message code='spring.security.ui.login.loggingYouIn'/>";
var loggedInAsWithPlaceholder = "<g:message code='spring.security.ui.login.loggedInAs' args='["{0}"]'/>";
</asset:script>
