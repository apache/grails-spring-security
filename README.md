<!--
SPDX-License-Identifier: Apache-2.0

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

[![Java CI](https://github.com/apache/grails-spring-security/actions/workflows/gradle.yml/badge.svg)](https://github.com/apache/grails-spring-security/actions/workflows/gradle.yml)

Grails Spring Security
======================

See [documentation](https://apache.github.io/grails-spring-security/) for detailed information.

### Building

To build this project from source, first bootstrap gradle:

     cd gradle-bootstrap
     gradle
     cd -

After bootstrap the project, you can build it with the command:

     ./gradlew build

To run the build only, and skip the tests, run:

     ./gradlew build -PskipTests

Then publish the jar files to mavenLocal for usage:

    ./gradlew publishToMavenLocal

### Branch structure 

- `7.0.x` compatible with Grails 7
- `6.0.x` compatible with Grails 6
- `5.0.x` compatible with Grails 5
- `4.0.x` compatible with Grails 4
- `3.3.x` compatible with Grails 3.3.x
- `3.2.x` compatible with Grails 3.2.x

### Spring Boot Auto-Configuration

The plugin automatically excludes Spring Boot's servlet security auto-configuration classes that conflict with the Grails Spring Security plugin. No manual `spring.autoconfigure.exclude` entries are needed.

**Configuration contract:** while this exclusion is enabled (the default), `grails.plugin.springsecurity.*` is the authoritative configuration source for the application's security. Spring Boot's `spring.security.*` properties are *not* merged into the plugin configuration and are *not* applied by Boot's auto-configuration. Use the plugin's keys, not Spring Boot's, to configure security when this plugin is active.

#### Coexistence with the component-based Spring Security configuration model

Spring Security 5.7 deprecated and Spring Security 6 removed `WebSecurityConfigurerAdapter`, replacing it with a component-based configuration model that registers individual `@Bean` components (see [Spring Security without the WebSecurityConfigurerAdapter](https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter)). This plugin pre-dates that model and provides equivalent functionality through the `grails.plugin.springsecurity.*` configuration namespace, but it now **blends** the most common component-based patterns automatically. You can configure security from either side (or both) and the effective configuration is the union of both sources.

| Spring component-based pattern | Blending behaviour with this plugin active | Disable via |
|---|---|---|
| `@Bean SecurityFilterChain` | **Auto-merged** into the plugin's `FilterChainProxy`; user chains are prepended (higher precedence) so their typically more-specific request matchers win against the plugin's catch-all chain. | `grails.plugin.springsecurity.componentBased.autoMergeSecurityFilterChain: false` |
| `@Bean AuthenticationProvider` | **Auto-merged** into the plugin's `authenticationManager` (`ProviderManager`); user providers are appended so the plugin's primary GORM-backed provider runs first; providers already declared via `providerNames` are not re-added. | `grails.plugin.springsecurity.componentBased.autoMergeAuthenticationProviders: false` |
| `@Bean InMemoryUserDetailsManager` / `JdbcUserDetailsManager` (or any extra `UserDetailsService`) | **Auto-chained** behind the plugin's primary `GormUserDetailsService`; queried in bean-name order if the GORM lookup throws `UsernameNotFoundException`. The chained service is wired into `daoAuthenticationProvider`. | `grails.plugin.springsecurity.componentBased.autoChainUserDetailsServices: false` |
| `spring.security.user.name` / `spring.security.user.password` / `spring.security.user.roles` | If `spring.security.user.name` is set, an `InMemoryUserDetailsManager` is created from those properties (mimicking what Spring Boot's `UserDetailsServiceAutoConfiguration` would have done) and chained behind the plugin's primary user lookup. | `grails.plugin.springsecurity.componentBased.bridgeSpringSecurityUserProperties: false` |
| `@Bean WebSecurityCustomizer` | Still a no-op. The plugin does not use Spring's `WebSecurity` builder. To exclude URLs from security checks, use `grails.plugin.springsecurity.staticRules` with `permitAll`, or `grails.plugin.springsecurity.ipRestrictions`. | n/a |
| `@Bean AuthenticationManager` | Conflicts with the plugin's `authenticationManager` (`ProviderManager`) bean by name. Use `@Bean AuthenticationProvider` (auto-merged - see above) or `grails.plugin.springsecurity.providerNames` instead. | n/a |
| LDAP factory beans (`EmbeddedLdapServerContextSourceFactoryBean`, `LdapBindAuthenticationManagerFactory`, `LdapPasswordComparisonAuthenticationManagerFactory`) | Coexist but are not wired into the plugin's authentication providers. | Use the `grails-spring-security-ldap` plugin and the `grails.plugin.springsecurity.ldap.*` configuration. |

To delegate the entire servlet security stack to Spring Boot's component-based model (and stop using the plugin's `grails.plugin.springsecurity.*` configuration), disable the excluder via the property below.

To disable this automatic exclusion (e.g. if you want to delegate the entire servlet security stack to Spring Boot instead of the plugin), add the following to `application.yml`:

```yml
grails:
  plugin:
    springsecurity:
      excludeSpringSecurityAutoConfiguration: false
```

Disabling the exclusion is intentionally a footgun: the plugin can no longer guarantee that its filter chain is the only servlet security stack in the application context, and a startup `WARN` will be logged.

If you are on an older version of the plugin that does not support automatic exclusion, you can manually exclude the conflicting classes.

For Grails 8 / Spring Boot 4 (security auto-configurations live under `org.springframework.boot.security.*`):

```yml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration
      - org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration
      - org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration
      - org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration
      - org.springframework.boot.security.autoconfigure.actuate.web.servlet.ManagementWebSecurityAutoConfiguration
      - org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration
      - org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration
      - org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration
      - org.springframework.boot.security.saml2.autoconfigure.Saml2RelyingPartyAutoConfiguration
      - org.springframework.boot.security.oauth2.server.authorization.autoconfigure.servlet.OAuth2AuthorizationServerAutoConfiguration
      - org.springframework.boot.security.oauth2.server.authorization.autoconfigure.servlet.OAuth2AuthorizationServerJwtAutoConfiguration
```

For Grails 7 / Spring Boot 3 (security auto-configurations live under `org.springframework.boot.autoconfigure.security.*`):

```yml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
      - org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
      - org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
      - org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration
      - org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration
      - org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration
      - org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
```
