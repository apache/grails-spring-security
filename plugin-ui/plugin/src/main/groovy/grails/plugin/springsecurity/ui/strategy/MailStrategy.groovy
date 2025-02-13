/* Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.springsecurity.ui.strategy

/**
 * To avoid depending on the mail plugin an instance of a class implementing this
 * interface is registered as the 'uiMailStrategy' Spring bean. By default
 * the plugin uses MailPluginMailStrategy, but you can register your own bean in
 * resources.groovy with the same bean name to customize how emails are sent or to
 * use something other than the mail plugin.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
interface MailStrategy {

	def sendVerifyRegistrationMail(Map params)

	def sendForgotPasswordMail(Map params)
}
