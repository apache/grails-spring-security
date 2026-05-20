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

# Threat Model - Apache Grails Spring Security

## §1 Header

- **Project**: Apache Grails Spring Security (`apache/grails-spring-security`)
- **Version binding**: 8.0.x branch. A report against version *N* is triaged against this document as it stood at *N*, not at HEAD.
- **Date**: 2026-01
- **Author**: Apache Grails PMC and contributors (initial draft).
- **Status**: **DRAFT** - not yet ratified by maintainers. Open questions in §14 must be resolved before this document is binding.
- **Reporting cross-reference**: findings that may violate a property claimed in §8 should be reported privately per [`SECURITY.md`](./SECURITY.md) (which routes to the [ASF Security Team](https://www.apache.org/security/)). Findings that fall under §3 (out of scope), §9 (disclaimed properties), or §11a (known non-findings) will be closed publicly citing the relevant section of this document.
- **Provenance legend**: every non-trivial claim is tagged.
  - *(documented)* - stated in the project's own docs (per-plugin `docs/src/docs/*.adoc` files, `README.md`).
  - *(maintainer)* - stated by a maintainer in response to a question from this drafting process.
  - *(inferred)* - reasoned from code structure, absence of a feature, or general domain knowledge. Each must have a matching entry in §14.

**Project description**: Apache Grails Spring Security is a family of eight Grails plugins (plus a compatibility shim) that wire Spring Security 7.x into a Grails 8.x application. The plugins provide authentication (form, basic, digest, X.509, remember-me, LDAP, CAS, OAuth2, JWT/REST), authorization (role-based via `@Secured` / Requestmap / static rules, object-level via Spring Security ACL), session management (fixation prevention, optional concurrent control), channel security (HTTP/HTTPS redirect), IP restrictions, and a UI plugin shipping CRUD controllers for users, roles, requestmaps, ACL entries, registration, and password reset. The unit of trust modeled here is "a Grails application that has installed one or more of these plugins"; the plugins are not deployed as a standalone network service. *(documented: [README.md](./README.md), per-plugin `docs/src/docs/introduction.adoc`)*

---

## §2 Scope and intended use

**Primary intended use**: provide authentication, authorization, session management, and account-management UI for Grails web applications running on Spring Boot 4 / Spring Security 7. *(documented: [README.md](./README.md))*

**Caller roles**:

| Role | Trust level | Description |
|---|---|---|
| **Unauthenticated HTTP client** | **Untrusted** | Sends HTTP requests to a deployed Grails application. Source of all attacker-controllable input on the auth-ingress side. *(inferred)* |
| **Authenticated low-privilege HTTP client** | **Partially trusted** | Has completed authentication via one of the supported flows. Holds a valid `Authentication` in the `SecurityContext` with limited authorities. Attempts horizontal/vertical privilege escalation. *(inferred)* |
| **Application developer / operator** | **Trusted** | Writes controllers, services, GSP templates; configures `application.yml` / `application.groovy` / Requestmap rows; chooses which plugins to install. *(inferred)* |
| **Plugin / `grails` profile author** | **Trusted-by-association** | Author of a third-party Grails plugin or `grails` profile that the operator installs alongside this plugin. Code from a plugin runs with full application privileges. The framework does not isolate plugin code. *(inferred)* |
| **External identity provider** | **Trusted-by-association** | An LDAP server, CAS server, or OAuth2 authorization server selected by the operator. Compromise of any of these is equivalent to authentication bypass within the application. *(inferred)* |

### Component-family table

The repository ships nine independent Gradle subprojects. The model carves them into in-model and out-of-model families:

| Family | Representative entry point(s) | Touches outside process? | In or out of model |
|---|---|---|---|
| Core authentication / authorization | [`SpringSecurityCoreGrailsPlugin`](./plugin-core/plugin/src/main/groovy/grails/plugin/springsecurity/SpringSecurityCoreGrailsPlugin.groovy), [`SpringSecurityUtils`](./plugin-core/plugin/src/main/groovy/grails/plugin/springsecurity/SpringSecurityUtils.groovy), [`AbstractFilterInvocationDefinition`](./plugin-core/plugin/src/main/groovy/grails/plugin/springsecurity/web/access/intercept/AbstractFilterInvocationDefinition.groovy) | Yes - HTTP via Spring MVC, DB via GORM | **In** |
| GORM user store | [`GormUserDetailsService`](./plugin-core/plugin/src/main/groovy/grails/plugin/springsecurity/userdetails/GormUserDetailsService.groovy), [`GormPersistentTokenRepository`](./plugin-core/plugin/src/main/groovy/grails/plugin/springsecurity/web/authentication/rememberme/GormPersistentTokenRepository.groovy) | Yes - DB via GORM | **In** |
| Spring Boot autoconfig exclusion | [`SecurityAutoConfigurationExcluder`](./plugin-core/plugin/src/main/groovy/grails/plugin/springsecurity/SecurityAutoConfigurationExcluder.groovy), [`ComponentBasedConfigBlender`](./plugin-core/plugin/src/main/groovy/grails/plugin/springsecurity/componentbased/ComponentBasedConfigBlender.groovy) | No (startup wiring) | **In** |
| ACL (object-level permissions) | [`SpringSecurityAclGrailsPlugin`](./plugin-acl/plugin/src/main/groovy/grails/plugin/springsecurity/acl/SpringSecurityAclGrailsPlugin.groovy), [`GormAclLookupStrategy`](./plugin-acl/plugin/src/main/groovy/grails/plugin/springsecurity/acl/jdbc/GormAclLookupStrategy.groovy) | Yes - DB via GORM | **In** |
| Compatibility shim (re-implements Spring Security 5.x classes) | [`FilterSecurityInterceptor`](./spring-security-compat/src/main/groovy/org/springframework/security/web/access/intercept/FilterSecurityInterceptor.groovy), [`AffirmativeBased`](./spring-security-compat/src/main/groovy/org/springframework/security/access/vote/AffirmativeBased.groovy), [`AntPathRequestMatcher`](./spring-security-compat/src/main/groovy/org/springframework/security/web/util/matcher/AntPathRequestMatcher.groovy), [`RunAsManagerImpl`](./spring-security-compat/src/main/groovy/org/springframework/security/access/intercept/RunAsManagerImpl.groovy) | No | **In** |
| LDAP authentication | [`SpringSecurityLdapGrailsPlugin`](./plugin-ldap/plugin/src/main/groovy/grails/plugin/springsecurity/ldap/SpringSecurityLdapGrailsPlugin.groovy), [`GrailsLdapAuthoritiesPopulator`](./plugin-ldap/plugin/src/main/groovy/grails/plugin/springsecurity/ldap/userdetails/GrailsLdapAuthoritiesPopulator.groovy) | Yes - LDAP/LDAPS network calls | **In** |
| CAS single sign-on | [`SpringSecurityCasGrailsPlugin`](./plugin-cas/plugin/src/main/groovy/grails/plugin/springsecurity/cas/SpringSecurityCasGrailsPlugin.groovy) | Yes - HTTPS callbacks to CAS server | **In** |
| OAuth2 client | [`OAuth2AbstractProviderService`](./plugin-oauth2/plugin/src/main/groovy/grails/plugin/springsecurity/oauth2/service/OAuth2AbstractProviderService.groovy), [`OAuth2ProviderConfiguration`](./plugin-oauth2/plugin/src/main/groovy/grails/plugin/springsecurity/oauth2/util/OAuth2ProviderConfiguration.groovy) | Yes - HTTPS calls to OAuth2 provider | **In** |
| REST / JWT authentication | [`RestAuthenticationFilter`](./plugin-rest/spring-security-rest/src/main/groovy/grails/plugin/springsecurity/rest/RestAuthenticationFilter.groovy), [`RestTokenValidationFilter`](./plugin-rest/spring-security-rest/src/main/groovy/grails/plugin/springsecurity/rest/RestTokenValidationFilter.groovy), [`JwtService`](./plugin-rest/spring-security-rest/grails-app/services/grails/plugin/springsecurity/rest/JwtService.groovy) | Yes - optional Redis/Memcached network | **In** |
| REST token storage backends | [`GormTokenStorageService`](./plugin-rest/spring-security-rest-gorm/src/main/groovy/grails/plugin/springsecurity/rest/token/storage/GormTokenStorageService.groovy), [`RedisTokenStorageService`](./plugin-rest/spring-security-rest-redis/src/main/groovy/grails/plugin/springsecurity/rest/token/storage/RedisTokenStorageService.groovy), [`MemcachedTokenStorageService`](./plugin-rest/spring-security-rest-memcached/src/main/groovy/grails/plugin/springsecurity/rest/token/storage/memcached/MemcachedTokenStorageService.groovy), [`GrailsCacheTokenStorageService`](./plugin-rest/spring-security-rest-grailscache/src/main/groovy/grails/plugin/springsecurity/rest/token/storage/GrailsCacheTokenStorageService.groovy) | Yes - DB / Redis / Memcached | **In** |
| UI plugin (HTTP controllers, the ONLY plugin that ships endpoints) | [`UserController`](./plugin-ui/plugin/grails-app/controllers/grails/plugin/springsecurity/ui/UserController.groovy), [`RoleController`](./plugin-ui/plugin/grails-app/controllers/grails/plugin/springsecurity/ui/RoleController.groovy), [`RequestmapController`](./plugin-ui/plugin/grails-app/controllers/grails/plugin/springsecurity/ui/RequestmapController.groovy), [`RegisterController`](./plugin-ui/plugin/grails-app/controllers/grails/plugin/springsecurity/ui/RegisterController.groovy), [`AclEntryController`](./plugin-ui/plugin/grails-app/controllers/grails/plugin/springsecurity/ui/AclEntryController.groovy), [`SecurityInfoController`](./plugin-ui/plugin/grails-app/controllers/grails/plugin/springsecurity/ui/SecurityInfoController.groovy) | Yes - HTTP, SMTP (mail) | **In** |
| Examples / functional test apps | `plugin-*/examples/`, `plugin-rest/spring-security-rest-testapp-profile/` | n/a | **Out** - not shipped in plugin distributions. See §3. |
| Per-plugin documentation | `plugin-*/docs/src/docs/*.adoc` | n/a | **Out** - documentation source, not runtime code. |

The UI plugin is the only family in this repository that registers live HTTP endpoints in the host application; every other plugin contributes Spring beans, filters, domain classes, and configuration. Its threat profile is therefore qualitatively different and gets dedicated treatment in §11.

---

## §3 Out of scope (explicit non-goals)

The plugins **do not** attempt to defend against, and **do not** model, the following. Triagers may close findings citing this section.

- **Application controllers, services, and domain classes outside these plugins.** The plugins provide an authentication and authorization framework; the application's own HTTP endpoints and business logic are out of scope. *(inferred)*
- **Transport security (TLS).** Provided by the Spring Boot embedded container or by a reverse proxy in front of the application. The LDAP, CAS, and OAuth2 plugins assume TLS to the external IdP is configured by the operator. *(inferred)*
- **External identity provider implementations.** Vulnerabilities in the LDAP server (OpenLDAP, AD, ApacheDS), the CAS server (Apereo CAS, Jasig CAS), or any OAuth2 authorization server are triaged in those projects. The plugin's contract is to forward credentials and consume responses; it does not audit the IdP. *(inferred)*
- **Spring Security itself.** Triaged at <https://spring.io/projects/spring-security>. The plugins re-expose Spring Security's public APIs but do not own its threat model. The compatibility shim in `spring-security-compat/` is an exception: classes vendored under `org.springframework.security.*` package names but maintained in this repository ARE in model. *(inferred)*
- **ScribeJava, Nimbus JOSE+JWT, pac4j, Jedis, spymemcached.** Third-party libraries the plugins depend on. *(inferred)*
- **JDK, JDBC drivers, JVM vulnerabilities.** Upstream. *(inferred)*
- **Third-party Grails plugins.** Plugins outside this repository run with full application privileges; their threat models are the responsibility of their authors. *(inferred)*
- **Example applications under `plugin-*/examples/` and the `spring-security-rest-testapp-profile` skeleton.** Not shipped in plugin distributions. Functional-test fixtures only. *(inferred)*
- **Build-time supply chain** (Gradle plugin portal, Maven Central, signing, reproducible builds, GitHub Action pinning). Important, but not threat-model content per the Apache security-team rubric. *(inferred)*
- **Side-channel attacks** (timing, cache, power, micro-architectural). No constant-time guarantees are made anywhere in the plugins. *(inferred)*
- **Denial of service via large bcrypt work factor on attacker-supplied passwords.** `password.bcrypt.logrounds` defaults to 10 and `security.ui.password.maxLength` defaults to 64; raising either to a value that lets an attacker exhaust CPU on `/login` is a `non-default-build` operator-responsibility issue. *(inferred)*

---

## §4 Trust boundaries and data flow

The principal trust boundaries modeled here are:

1. **HTTP request boundary** - data crossing from an unauthenticated end user into the Spring Security filter chain.
2. **Authenticated request boundary** - data crossing from a low-privilege authenticated session into authorization decisions (`@Secured`, Requestmap, ACL).
3. **External IdP boundary** - data crossing from an LDAP / CAS / OAuth2 server response into a local `Authentication` object.
4. **Token store boundary** - data crossing into / out of Redis, Memcached, or the GORM database for REST/JWT session lookup.

### Primary data flow A: form login (default chain)

```
[Unauthenticated HTTP client]
        |
        v  (HTTPS expected; operator's responsibility)
[Embedded servlet container]                                                   <-- boundary: not plugin
        |
        v
[FilterChainProxy / springSecurityFilterChain]                                 <-- HTTP enters plugin
        |
        v
[channelProcessingFilter (if secureChannel.definition non-empty)]              <-- HTTPS enforcement gate
        |
        v
[securityContextPersistenceFilter]
        |
        v
[logoutFilter]
        |
        v
[ipAddressFilter (if ipRestrictions non-empty)]                                 <-- uses request.remoteAddr ONLY
        |
        v
[authenticationProcessingFilter]  <-- credentials extracted from form POST
        |     |
        |     +--> [authenticationManager (ProviderManager)]
        |            |
        |            +--> [daoAuthenticationProvider --> GormUserDetailsService --> DB]
        |            +--> [anonymousAuthenticationProvider]
        |            +--> [rememberMeAuthenticationProvider]
        |            +--> [merged user-defined providers (ComponentBasedConfigBlender)]
        v
[rememberMeAuthenticationFilter (cookie path)]
        |
        v
[anonymousAuthenticationFilter]
        |
        v
[exceptionTranslationFilter]
        |
        v
[filterInvocationInterceptor (FilterSecurityInterceptor)]                       <-- authorization gate
        |     |
        |     +--> [objectDefinitionSource: @Secured | Requestmap | InterceptUrlMap]
        |     +--> [accessDecisionManager: AuthenticatedVetoableDecisionManager]
        |            +--> voters: authenticatedVoter, roleVoter (RoleHierarchy), webExpressionVoter (SpEL), closureVoter
        v
[Application controller action]                                                 <-- developer code
```

### Primary data flow B: REST/JWT chain (when plugin-rest is installed)

```
[Unauthenticated HTTP client]
        |
        v  POST /api/login + JSON body {username, password}
[RestAuthenticationFilter]
        |    DefaultJsonPayloadCredentialsExtractor parses body
        |
        v
[authenticationManager]  (same providers as flow A)
        |    on success ->
        v
[tokenGenerator (SignedJwtTokenGenerator | SecureRandomTokenGenerator | EncryptedJwtTokenGenerator)]
        |
        v
[tokenStorageService.storeToken(token, userDetails)]
        |    JWT backend: NO-OP (stateless)
        |    GORM/Redis/Memcached/Grails Cache: persists serialized principal
        v
[RestAuthenticationSuccessHandler]  -->  JSON response body with access_token + refresh_token

[Subsequent request]
        |    Authorization: Bearer <token>
        v
[RestTokenValidationFilter]
        |
        v
[RestAuthenticationProvider]
        |    JwtService.parse(token):
        |       SignedJWT  -> MACVerifier(jwtSecret)
        |       EncryptedJWT -> RSADecrypter(privateKey)
        |       PlainJWT     -> JOSEException ONLY if (jwtSecret || keyProvider) is non-null
        |    tokenStorageService.loadUserByToken(token) -> UserDetails
        v
[SecurityContextHolder.getContext().setAuthentication(...)]
        |
        v
[filterInvocationInterceptor]  (authorization gate, same as flow A)
```

The trust transition occurs at the entry filter (`authenticationProcessingFilter` for form login, `RestAuthenticationFilter` for REST). Within the plugins, **request parameters, headers, cookies, request bodies, and bearer tokens are treated as attacker-controlled until a verified `Authentication` is in the context**. The token validation path is the matching transition on the inbound side of every subsequent request.

### Reachability preconditions per component family

A triager applies these tests before deciding a finding is in-model:

| Component family | Reachability precondition for a finding to be in-model |
|---|---|
| Core filter chain | Reachable from an HTTP request with no developer-authored guard preceding it. *(inferred)* |
| Authorization (`FilterSecurityInterceptor`, `AbstractFilterInvocationDefinition`) | Reachable when a request URL falls under a `staticRules` / Requestmap / `@Secured` entry, OR when `rejectIfNoRule` is `true` (default) AND the URL is uncovered. *(inferred)* |
| GORM user store | Reachable from every authentication attempt. *(inferred)* |
| ACL | Reachable from any method secured with `@PreAuthorize("hasPermission(...)")` or `@PostFilter`, AND the application has populated `AclObjectIdentity`/`AclEntry` rows. *(inferred)* |
| Compat shim (`spring-security-compat`) | Reachable on every secured-method invocation; the shim's classes are the actual runtime classes, not Spring Security's. *(inferred)* |
| LDAP | Reachable from authentication when `ldap` plugin is installed AND a request reaches `LdapAuthenticationProvider`. *(inferred)* |
| CAS | Reachable from any HTTP request when `cas` plugin is installed (the SLO filter runs at `HIGHEST_PRECEDENCE`). *(inferred)* |
| OAuth2 | Reachable from `/oauth2/authenticate/{provider}` and `/oauth2/callback/{provider}` when the `oauth2` plugin is installed. *(inferred)* |
| REST / JWT | Reachable from any HTTP request when `spring-security-rest` is installed (the validation filter runs on every request unless `active: false`). *(inferred)* |
| UI controllers | Reachable from any HTTP request; **the UI plugin ships no default protection for its own endpoints**. Reachability is therefore unconditional unless the operator configures Requestmap/staticRules entries. *(inferred)* |
| Token storage backends | Reachable on every REST request via `loadUserByToken`. *(inferred)* |

The reachability column for **UI controllers** is the load-bearing claim of this section: shipping CRUD controllers without default authorization means that installing the UI plugin and forgetting to configure Requestmap rows is functionally equivalent to publishing an admin console on the open internet. A report against `/user/save`, `/role/save`, `/requestmap/save`, or `/aclEntry/save` is in-model if the operator did not explicitly add a protection rule. See §11.

---

## §5 Assumptions about the environment

**Runtime**:

- JDK 21 or higher. *(documented: [README.md](./README.md), upstream Grails 8 requirement)*
- Apache Groovy 4.0.x. *(documented: upstream Grails 8 requirement)*
- Spring Boot 4.0.x / Spring Framework 7.0.x. *(documented: upstream Grails 8 requirement)*
- Spring Security 7.0.x. *(documented: [README.md](./README.md))*
- Jakarta EE 10 servlet API. *(documented: upstream Grails 8 requirement)*

**Operator-controlled environment**:

- The application is deployed by a trusted operator on hardware and OS the operator controls. *(inferred)*
- The embedded servlet container is fronted by, or itself provides, transport security (TLS). *(inferred)*
- All credentials that cross network boundaries to external IdPs (LDAP manager DN/password, CAS service URL, OAuth2 client secret) are transmitted over TLS. *(inferred)*
- Redis / Memcached token-storage backends, when used, are network-isolated; the plugins assume no untrusted client can write directly to those stores. *(inferred)* (§14 wave 1)
- The application classpath contains only artifacts the operator/developer chose - no untrusted JAR is loaded at runtime. *(inferred)*
- `application.yml`, `application.groovy`, and any `grails.config.locations` are sourced from operator-trusted storage. The Grails framework's threat model on these surfaces applies; see the framework's THREAT_MODEL.md §9 false-friend on `grails.config.locations`. *(inferred)*

**Concurrency**:

- The plugins assume a thread-per-request servlet model. The `SecurityContextHolder` is `ThreadLocal`-backed by Spring Security. *(documented: upstream Spring Security)*
- The reactive (`WebFlux`) variants of Spring Boot security auto-configuration are intentionally NOT excluded by `SecurityAutoConfigurationExcluder`; the plugins target servlet-stack deployments only. *(documented: [SecurityAutoConfigurationExcluder.groovy](./plugin-core/plugin/src/main/groovy/grails/plugin/springsecurity/SecurityAutoConfigurationExcluder.groovy))*

### What the plugins do NOT do to their host

These are **negative claims** about plugin behavior. By the rubric they are the lowest-confidence claims in the model and the highest-priority targets for maintainer confirmation (see §14).

- The plugins do not bind sockets directly. All network listening is delegated to the Spring Boot embedded container. *(inferred)*
- The plugins do not spawn child processes from the runtime layer. *(inferred)*
- The plugins do not install JVM signal handlers. *(inferred)*
- The plugins do not read environment variables beyond `SPRING_SECURITY_*` and Spring Boot's standard set. *(inferred)*
- The plugins do not mutate global JVM state at runtime beyond the `SecurityContextHolder` ThreadLocal. *(inferred)*
- The plugins do not write to stdout or stderr at runtime beyond SLF4J-routed logging. The OAuth2 `debug` flag is the documented exception - when `oauth2.{provider}.debug = true`, raw token traffic is logged. *(documented: [OAuth2ProviderConfiguration.groovy](./plugin-oauth2/plugin/src/main/groovy/grails/plugin/springsecurity/oauth2/util/OAuth2ProviderConfiguration.groovy))*
- The plugins do not load classes from network locations at runtime. *(inferred)*
- The plugins do not deserialize attacker-controlled `ObjectInputStream` data **when configured per §10**; see §11a for the documented sinks and their preconditions.

---

## §5a Build-time and configuration variants

The plugins expose many configuration knobs whose values change which §8 properties hold. The defaults below come from `DefaultSecurityConfig.groovy` (plugin-core), `DefaultLdapSecurityConfig.groovy`, `DefaultCasSecurityConfig.groovy`, `DefaultSpringSecurityOAuth2Config.groovy`, `DefaultRestSecurityConfig.groovy`, and `DefaultUiSecurityConfig.groovy`. "Maintainer stance" is a §14 target.

### Core plugin

| Knob | Default | Effect on the model | Maintainer stance |
|---|---|---|---|
| `password.algorithm` | `bcrypt` *(documented: [hashing.adoc](./plugin-core/docs/src/docs/passwords/hashing.adoc))* | Default password encoder ID. Any string accepted by Spring Security's `DelegatingPasswordEncoder` is valid; the plugin enumerates `bcrypt`, `pbkdf2`, `scrypt`, `argon2`, `ldap`, `MD4`, `MD5`, `SHA-1`, `SHA-256`, `noop`. Setting to `noop` stores cleartext passwords. | **§14 wave 1** |
| `password.bcrypt.logrounds` | `10` *(documented: [hashing.adoc](./plugin-core/docs/src/docs/passwords/hashing.adoc))* | Bcrypt work factor. The docs note the default is "lower for testing speed" and operators "should set them manually" - this is a security-critical configuration the docs explicitly delegate. | **§14 wave 1** - is 10 the supported production default or a test-only default? |
| `password.hash.iterations` | `10000` *(documented: [hashing.adoc](./plugin-core/docs/src/docs/passwords/hashing.adoc))* | Iteration count for message-digest encoders (not bcrypt/pbkdf2). | **§14 wave 2** |
| `useSessionFixationPrevention` | `true` *(documented: [sessionFixation.adoc](./plugin-core/docs/src/docs/sessionFixation.adoc))* | Wires `SessionFixationProtectionStrategy`. Setting to `false` substitutes `NullAuthenticatedSessionStrategy`, removing all session-fixation protection. | **§14 wave 1** |
| `rejectIfNoRule` | `true` *(documented: [requestMappings.adoc](./plugin-core/docs/src/docs/requestMappings.adoc))* | Pessimistic URL coverage: unmatched URLs are denied. Setting to `false` is `OUT-OF-MODEL: non-default-build` for missing-authz reports. | **§14 wave 1** |
| `fii.rejectPublicInvocations` | `true` *(documented: [requestMappings.adoc](./plugin-core/docs/src/docs/requestMappings.adoc))* | If both `rejectIfNoRule: false` AND `fii.rejectPublicInvocations: false`, `FilterSecurityInterceptor` passes uncovered URLs through with no check. | **§14 wave 1** |
| `excludeSpringSecurityAutoConfiguration` | `true` *(documented: [README.md](./README.md))* | When `true`, [`SecurityAutoConfigurationExcluder`](./plugin-core/plugin/src/main/groovy/grails/plugin/springsecurity/SecurityAutoConfigurationExcluder.groovy) suppresses 11 conflicting Spring Boot auto-configurations. Setting to `false` is intentionally "a footgun"; both filter chains run with undefined precedence. *(documented: [README.md](./README.md))* | **§14 wave 1** |
| `componentBased.autoMergeSecurityFilterChain` | `true` *(documented: [README.md](./README.md))* | User-defined `@Bean SecurityFilterChain` beans are **prepended** to the plugin's chain list (higher precedence). A user catch-all chain (`/**`) shadows all plugin rules. | **§14 wave 2** |
| `componentBased.autoMergeAuthenticationProviders` | `true` *(documented: [README.md](./README.md))* | User `@Bean AuthenticationProvider` beans appended after the GORM provider. | **§14 wave 2** |
| `componentBased.bridgeSpringSecurityUserProperties` | `true` *(documented: [README.md](./README.md))* | If `spring.security.user.name` is set, an `InMemoryUserDetailsManager` with `{noop}` password prefix is wired and chained. A developer-convenience property left in production creates a credential with no hashing. | **§14 wave 1** |
| `rememberMe.persistent` | `false` *(inferred)* | When `false`, `TokenBasedRememberMeServices` is used; remember-me cookies are signed with MD5-HMAC over `username:expiry:password`. When `true`, `PersistentTokenBasedRememberMeServices` + `GormPersistentTokenRepository` provides DB-side token-theft detection. MD5-HMAC is cryptographically weak relative to modern primitives. *(inferred)* | **§14 wave 2** |
| `rememberMe.key` | none (operator-set) *(inferred)* | HMAC key for non-persistent remember-me. Null or short keys make cookie-signature forgery practical. The plugin does not enforce a minimum length. | **§14 wave 1** |
| `cacheUsers` | `false` *(documented: [locking.adoc](./plugin-core/docs/src/docs/passwords/locking.adoc))* | When `true`, account lock/disable changes are bypassed until the cache entry is manually evicted via `userCache.removeUserFromCache(username)`. The docs explicitly warn about this. | **§14 wave 2** |
| `secureChannel.useHeaderCheckChannelSecurity` | `false` *(documented: [channelSecurity.adoc](./plugin-core/docs/src/docs/channelSecurity.adoc))* | When `false`, channel decision uses `request.isSecure()` only; behind a TLS-terminating proxy this returns `false` even for HTTPS clients. When `true`, the plugin checks `X-Forwarded-Proto`. `PortResolverImpl` does NOT consult `X-Forwarded-Port` regardless of this flag - the redirect URL will carry the backend port. | **§14 wave 1** |
| `ipRestrictions` | empty *(inferred)* | When non-empty, [`IpAddressFilter`](./plugin-core/plugin/src/main/groovy/grails/plugin/springsecurity/web/filter/IpAddressFilter.groovy) enforces CIDR matching against `request.remoteAddr` only. **Does not consult `X-Forwarded-For`.** Behind a reverse proxy, IP restrictions are bypassed by every request unless the proxy preserves the source IP at the TCP layer. | **§14 wave 1** |
| `useSecurityEventListener` | `false` *(inferred)* | When `true`, configured Groovy `Closure` properties (`onAuthenticationSuccessEvent`, etc.) execute on each auth event. If `application.groovy` is sourced from attacker-writable storage, this is an arbitrary-code-execution sink. | **§14 wave 2** |
| `useRunAs` | `false` *(inferred)* | When `true`, `RunAsManagerImpl` substitutes elevated roles for the duration of secured-method calls. The substitution token is not HMAC-signed in the compat shim; key validation occurs only on the return path. | **§14 wave 2** |

### ACL plugin

| Knob | Default | Effect on the model |
|---|---|---|
| `acl.permissionClass` | `BasePermission` *(inferred)* | If supplied as a `String`, loaded via `classLoader.loadClass(name)`. A misconfigured class name silently changes the permission-mask semantics. |
| `acl.authority.changeOwnership` / `modifyAuditingDetails` / `changeAclDetails` | configured authority strings *(inferred)* | Required authorities to mutate ACL ownership / audit / DACL. **Granting/revoking individual ACEs has no built-in caller authorization beyond what the application places on its own service methods.** |

### LDAP plugin

| Knob | Default | Effect on the model |
|---|---|---|
| `ldap.context.server` | `ldap://localhost:389` *(documented: [`DefaultLdapSecurityConfig.groovy`](./plugin-ldap/plugin/grails-app/conf/DefaultLdapSecurityConfig.groovy))* | Default is plaintext LDAP. Manager DN + password and user credentials transit in cleartext unless changed to `ldaps://` or wrapped in StartTLS. The plugin does NOT wire StartTLS; operators needing it must supply a custom `authenticationStrategy` bean. |
| `ldap.authenticator.useBind` | `true` *(documented)* | Bind authentication (safer). Setting `false` selects `PasswordComparisonAuthenticator`, requiring the manager account to read `userPassword` from the directory. |
| `ldap.context.referral` | `null` *(documented)* | When set to `'follow'`, JNDI follows LDAP referrals to arbitrary servers, including attacker-controlled ones. |
| `ldap.authorities.groupSearchFilter` | `'uniquemember={0}'` *(documented)* | `{0}` is the user DN (Spring Security LDAP encodes it). Custom filter templates using `{1}` (raw username) are NOT escaped; an unsanitized username would produce LDAP filter injection. |
| `ldap.auth.hideUserNotFoundExceptions` | `true` *(documented)* | When `false`, distinguishable error messages enable username enumeration. |

### CAS plugin

| Knob | Default | Effect on the model |
|---|---|---|
| `cas.serverUrlPrefix` | `null` (required) *(documented: [`DefaultCasSecurityConfig.groovy`](./plugin-cas/plugin/grails-app/conf/DefaultCasSecurityConfig.groovy))* | Operator-supplied. MUST be HTTPS; the plugin provides no certificate-pinning configuration. JVM default trust store applies. |
| `cas.serviceUrl` | `null` (required) *(documented)* | Static absolute URL. Not derived from `Host` / `X-Forwarded-Host`, so direct open-redirect via service manipulation is not reachable from a single request. Misconfiguration to HTTP is the realistic risk. |
| `cas.useSingleSignout` | `true` *(documented)* | Wires `SingleSignOutFilter` and forces `useSessionFixationPrevention = false`. **Enabling SLO disables session fixation prevention globally.** This is a documented trade-off in the Apereo CAS client. |
| `cas.key` | `'grails-spring-security-cas'` *(documented)* | Default shared secret for `CasAuthenticationProvider`. **Must be changed in production** - the default is published in the source tree. |
| `cas.proxyCallbackUrl` | `null` *(documented)* | When non-null, the application exposes a Proxy-Granting-Ticket receptor endpoint. The CAS server callback is trusted on the basis of TLS only; no additional origin validation is performed by the plugin. |

### OAuth2 plugin

| Knob | Default | Effect on the model |
|---|---|---|
| `oauth2.providers.{name}.apiKey` | none (required) *(inferred)* | Client ID; stored in application config. |
| `oauth2.providers.{name}.apiSecret` | none (required) *(inferred)* | Client secret. Stored as a plain string in config; transmitted on every token exchange. |
| `oauth2.providers.{name}.callbackUrl` | none (required) *(inferred)* | Static URL passed to `ServiceBuilder.callback()`. No allow-list; only the single configured URL is registered. |
| `oauth2.providers.{name}.debug` | `false` *(inferred)* | When `true`, ScribeJava logs raw OAuth traffic - including access tokens and refresh tokens - to stdout. Production deployments with `debug: true` leak credentials to the application log. |
| `oauth2.registration.roleNames` | `['ROLE_USER']` *(inferred)* | Roles granted to every OAuth2-authenticated user, regardless of provider claims. Including a privileged role here grants it to every social-login user. |
| OAuth2 state parameter (not configurable) | `java.util.Random` over 1,000,000-value space *(inferred)* | `OAuth2AbstractProviderService` generates state as `providerID + "-secret-" + Random.nextInt(999_999)`. **Not cryptographically secure; state-CSRF against the callback is feasible.** |
| OAuth2 PKCE | not implemented *(inferred)* | `ServiceBuilder.withPkce()` is never invoked. Authorization-code interception is not mitigated by PKCE at the plugin level. |

### REST / JWT plugin

| Knob | Default | Effect on the model |
|---|---|---|
| `rest.login.endpointUrl` | `/api/login` *(documented: [`DefaultRestSecurityConfig.groovy`](./plugin-rest/spring-security-rest/src/main/resources/DefaultRestSecurityConfig.groovy))* | Credential-intake endpoint. |
| `rest.login.useRequestParamsCredentials` | `false` *(documented)* | When `true`, credentials are read from query params, exposing them in URLs and logs. |
| `rest.token.generation.useSecureRandom` | `true` *(documented)* | Opaque-token entropy via `SecureRandom`. If set to `false` AND `useJwt: false`, `UUIDTokenGenerator` is used. |
| `rest.token.storage.jwt.useSignedJwt` | `true` *(documented: [tokenStorage.adoc](./plugin-rest/docs/src/docs/tokenStorage.adoc))* | HMAC-signed JWT. The signing algorithm is `rest.token.storage.jwt.algorithm` (default `HS256`); the plugin enforces no algorithm allow-list. |
| `rest.token.storage.jwt.secret` | `null` (required for HMAC mode) *(documented)* | HMAC key. Null + Nimbus key-length enforcement causes boot failure - **but** if `useSignedJwt: false` AND `useEncryptedJwt: false` AND the secret is null AND no key provider is configured, `JwtService.parse()` accepts `PlainJWT` tokens (`alg=none`). |
| `rest.token.storage.jwt.useEncryptedJwt` | `false` *(documented)* | When `true`, JWE is used (RSA-OAEP + AES-256-GCM). |
| `rest.token.storage.jwt.privateKeyPath` / `publicKeyPath` | `null` *(documented)* | DER-encoded RSA keys. When both null, `DefaultRSAKeyProvider` generates an ephemeral 2048-bit pair at every JVM start; tokens are unusable across restarts and across pods in a horizontal-scaling deployment. |
| `rest.token.storage.jwt.expiration` | `3600` seconds *(documented)* | Access-token lifetime. With stateless JWT, this is the only revocation mechanism. |
| `rest.token.storage.jwt.refreshExpiration` | `null` *(documented)* | Refresh-token lifetime. **Default is no expiry** - a leaked refresh token is valid forever. |
| `rest.token.validation.active` | `true` *(documented: [tokenValidation.adoc](./plugin-rest/docs/src/docs/tokenValidation.adoc))* | When `false`, `RestTokenValidationFilter` is a no-op pass-through; tokens are not validated. |
| `rest.token.validation.useBearerToken` | `true` *(documented)* | RFC 6750 Bearer header. |
| `rest.token.storage.gorm.tokenDomainClassName` | none *(documented)* | Loaded via `grailsApplication.getClassForName(...)`. Config-driven class loading. |

### UI plugin

| Knob | Default | Effect on the model |
|---|---|---|
| `security.ui.encodePassword` | `false` *(documented: [`DefaultUiSecurityConfig.groovy`](./plugin-ui/plugin/grails-app/conf/DefaultUiSecurityConfig.groovy))* | When `false`, the UI service stores submitted passwords **without encoding** when creating/updating users via the UI. **Must be set to `true` for production.** |
| `security.ui.register.requireEmailValidation` | `'true'` *(documented)* | When `false`, accounts are activated immediately; the email-confirmation gate is removed. |
| `security.ui.forgotPassword.requireForgotPassEmailValidation` | `'true'` *(documented)* | When `false`, the reset link is rendered into the HTTP response body instead of emailed. Reset token leaks to logs / browser history. |
| `security.ui.register.defaultRoleNames` | `['ROLE_USER']` *(documented)* | Roles assigned automatically on self-registration. Including a privileged role here grants administration rights to every self-registered account. |
| `security.ui.password.maxLength` | `64` *(documented)* | Maximum password length accepted by registration/reset forms. Bcrypt operates on the first 72 bytes of the password regardless. |
| `security.ui.password.validationRegex` | requires digit, letter, special *(documented)* | Password-complexity rule. Can be weakened to `.*`. |

There is **no compile-time `-D` define or build flag** that voids a §8 property; the model is invariant under build configuration. *(inferred)* (§14 wave 3)

---

## §6 Assumptions about inputs

The plugins' public input boundary is the HTTP request, plus responses from external IdPs. Per-input trust is summarized below.

### Per-input trust table

| Entry point / surface | Parameter | Attacker-controllable? | Caller (developer/operator) must enforce |
|---|---|---|---|
| Form login (`authenticationProcessingFilter`) | `j_username`, `j_password` (form POST body) | **Yes** | Configure a strong `password.algorithm` and `password.bcrypt.logrounds`; never set `password.algorithm = noop`. *(documented: [hashing.adoc](./plugin-core/docs/src/docs/passwords/hashing.adoc))* |
| Basic / Digest auth | `Authorization` header | **Yes** | Use only over TLS. Digest auth uses MD5 internally and is inherently weak; prefer Basic over TLS. *(inferred)* |
| Remember-me cookie | `remember-me` (cookie value) | **Yes** | Set a strong `rememberMe.key` (non-persistent mode) or use `rememberMe.persistent: true` (persistent mode). *(inferred)* |
| REST login (`RestAuthenticationFilter`) | JSON body `{username, password}` | **Yes** | Same as form login. *(documented: [authentication.adoc](./plugin-rest/docs/src/docs/authentication.adoc))* |
| Bearer token (`RestTokenValidationFilter`) | `Authorization: Bearer <token>` | **Yes** | Configure non-null `jwtSecret` OR `useSignedJwt: false` + valid RSA key pair. **Never both null.** *(documented: [tokenStorage.adoc](./plugin-rest/docs/src/docs/tokenStorage.adoc))* |
| OAuth2 callback | `code`, `state`, `error`, `error_description` (query params); `callback` (query param to `/oauth2/authenticate`) | **Yes** | Validate `state` against the session-stored value; the plugin's pac4j integration handles this. The `callback` query parameter is stored in session and used as the post-login redirect target - the application MUST validate against an allow-list. *(inferred)* |
| CAS callback | `ticket` (query param) | **Yes** | Validated against the CAS server over HTTPS. Trust collapses to the CAS server's TLS certificate. *(documented: [`SpringSecurityCasGrailsPlugin.groovy`](./plugin-cas/plugin/src/main/groovy/grails/plugin/springsecurity/cas/SpringSecurityCasGrailsPlugin.groovy))* |
| LDAP bind | username, password (forwarded from form/REST login) | **Yes (the values)**; **No (the LDAP server)** | The LDAP server is operator-selected and trusted. Username is encoded by Spring Security LDAP before substitution into the search filter when the default filter template uses `{0}`. Custom filters using `{1}` are not safe. *(inferred)* |
| UI form: registration | `RegisterCommand` (username, email, password, password2) | **Yes** | Enable `requireEmailValidation: true`; review `defaultRoleNames`; add rate limiting / CAPTCHA at the application layer (the plugin provides neither). *(inferred)* |
| UI form: forgot password | username | **Yes** | Account-enumeration risk - the action returns a distinguishable response for unknown usernames. *(inferred)* |
| UI form: reset password | `t` (token query param), new password | **Yes** | Reset tokens have NO expiry by default. *(inferred)* |
| UI form: User CRUD | All form fields, including `ROLE_*=on` checkboxes | **Yes** | The save/update actions pass `params` directly to `setProperties(params, ...)` - **mass-assignment via the params map**. The application MUST protect `/user/save`, `/user/update` with Requestmap or `@Secured`. *(inferred)* |
| UI form: Role CRUD | All form fields | **Yes** | No default protection. *(inferred)* |
| UI form: Requestmap CRUD | `url`, `configAttribute`, `httpMethod` | **Yes** | **Writing a Requestmap row rewrites the application's authorization policy.** No default protection on `/requestmap/save`. *(inferred)* |
| UI form: ACL entry CRUD | `mask`, `granting`, `sid`, `aceOrder` | **Yes** | No default protection. *(inferred)* |
| `Requestmap` table | `url`, `configAttribute` | **No - trusted developer/operator input** | Direct DB writes by the application must be authorized at the application layer. *(inferred)* |
| `Role` table | `authority` | **No - trusted developer/operator input** | Same. A row with `authority = 'ROLE_ADMIN'` is granted by inclusion in `Person.roles`. *(inferred)* |
| `AclSid` / `AclClass` / `AclObjectIdentity` / `AclEntry` tables | All fields | **No - trusted developer/operator input** | `AclClass.className` is loaded via `Class.forName(...)` in `GormAclLookupStrategy`; write access to that column is equivalent to arbitrary classloading. *(inferred)* |
| `application.groovy` / `application.yml` contents | All keys | **No - trusted operator input** | Same posture as the framework's THREAT_MODEL.md. Configuration is part of the TCB. *(inferred)* |
| `application.groovy` Groovy closures (`securityConfig.onAuthenticationSuccessEvent`, `securityConfig.ajaxCheckClosure`, voter closures) | Closure body | **No - trusted developer input** | Evaluated as Groovy code at startup or on each event. *(inferred)* |
| LDAP manager DN/password | from operator config | **No - trusted operator input** | Stored in `application.yml`/`application.groovy`. Operator must protect the config file. *(inferred)* |
| CAS server response | XML body returned by `serverUrlPrefix/serviceValidate` | **No (if TLS)**; **Yes (if HTTP or compromised TLS)** | TLS trust = JVM default trust store. No certificate pinning option. *(inferred)* |
| OAuth2 provider response | JSON body returned by token endpoint | **No (if TLS)**; **Yes (otherwise)** | Same. *(inferred)* |

### Size, shape, rate assumptions

- Bearer-token length: bounded by Nimbus JOSE+JWT parser; no plugin-level cap. *(inferred)*
- Password length: capped by `security.ui.password.maxLength` (default 64) on UI flows. Form login does not cap; bcrypt internally hashes only the first 72 bytes. *(documented: [`DefaultUiSecurityConfig.groovy`](./plugin-ui/plugin/grails-app/conf/DefaultUiSecurityConfig.groovy))*
- Request rate: no built-in rate limiting on `/login`, `/register`, `/register/forgotPassword`, or `/api/login`. Operators must add this at a proxy or via `bucket4j` / Spring Security's own brute-force protection. *(inferred)*

---

## §7 Adversary model

### In-scope adversary A: the unauthenticated HTTP end user

Capabilities:

- Crafts arbitrary HTTP requests against any URL pattern the application exposes.
- Sends arbitrary headers, cookies, query parameters, form bodies, JSON bodies, and bearer tokens.
- May submit credentials repeatedly (no built-in rate limiting; see §9).
- May replay state/code parameters in OAuth2 callbacks (state-CSRF is in scope; see §11).
- May replay or modify CSRF tokens against forms not protected by `withForm` (see §9).
- May post a crafted Requestmap / Role / AclEntry / User to any UI controller that lacks authorization (see §11).

### In-scope adversary B: the authenticated low-privilege user

Capabilities (in addition to adversary A):

- Holds a valid `Authentication` in the `SecurityContext` with limited authorities.
- May attempt vertical privilege escalation via:
  - Mass-assignment on `/user/update` (POST a `ROLE_ADMIN=on` parameter).
  - Direct POST to `/role/save` to create a role, then to `/user/update` to grant it.
  - Direct POST to `/requestmap/save` with `configAttribute: permitAll` to wipe the policy.
  - Direct POST to `/aclEntry/save` to grant ADMINISTRATION on any object identity.
  - Self-registration when `defaultRoleNames` includes a privileged role.

### In-scope adversary C: the compromised external identity provider

Capabilities:

- Returns crafted authentication responses (LDAP, CAS XML, OAuth2 JSON) to the plugin.
- LDAP referral target (if `referral: follow`).
- Issues unintended OAuth2 tokens / claims.

What this adversary does **not** have:

- Network position to MITM the TLS channel to the IdP - that is the operator's TLS posture.
- Read or write access to the application filesystem, classpath, env vars, or system properties.
- Co-location on the same JVM.

### Documented adversary-model statements

> **Authentication and authorization that the plugin enforces are only as trustworthy as the operator's `password.algorithm`, `jwtSecret`, `rememberMe.key`, `cas.key`, and OAuth2 `apiSecret` configuration values, and the TLS posture of the LDAP/CAS/OAuth2 connections.** A report that requires hostile control of any of those is `OUT-OF-MODEL: trusted-input` (§13). *(inferred)* (§14 wave 1)

> **Every HTTP endpoint shipped by the UI plugin is unprotected by default. Authorization is the operator's responsibility via Requestmap, `@Secured`, or `staticRules`.** A report that the UI controllers lack `@Secured` annotations is `BY-DESIGN: property-disclaimed` (§13), not a vulnerability. A report that the UI controllers are reachable in a deployment where the operator did configure protection IS in-model. *(inferred)* (§14 wave 1)

### Distributed-system adversary

Not applicable - the plugins target single-application servlet deployments. Stateless JWT can be used across replicas with a shared HMAC secret; this is a deployment topology, not a consensus protocol. *(inferred)*

### Out-of-scope adversaries

- **Local attacker with shell access on the application host.** Such an attacker can modify config, JARs, env vars, and the JVM itself. The plugins cannot defend against them. *(inferred)*
- **Compromised plugin / JAR on the application classpath.** Plugins run with full privileges by design. *(inferred)*
- **Compromised build environment.** Out of model per §3.
- **Co-tenant attacker on the same JVM.** Not modeled; one application per JVM is assumed. *(inferred)*
- **Attacker who controls a Grails plugin or `grails` profile downloaded by a developer running the CLI.** Same posture as the Grails framework's threat model; see that document. *(inferred)*
- **Network attacker capable of MITM against the operator's LDAP/CAS/OAuth2 TLS channels.** Out of scope - TLS trust is the operator's responsibility. *(inferred)*
- **Side-channel observers** (timing, cache, micro-architectural). *(inferred)*

---

## §8 Security properties the plugins provide

Each property is stated with its conditions, the symptom of a violation, a severity tier, and a provenance tag. Properties are scoped to a specific plugin where applicable.

| # | Property | Plugin | CWE | Conditions | Violation symptom | Severity | Provenance |
|---|---|---|---|---|---|---|---|
| P1 | **Passwords are stored as bcrypt hashes by default** via `DelegatingPasswordEncoder`. | core | [CWE-256](https://cwe.mitre.org/data/definitions/256.html) | `password.algorithm` is set to `bcrypt`, `pbkdf2`, `scrypt`, or `argon2`; not to `noop` or a message-digest algorithm. | Cleartext or unsalted-digest password storage. | **Security-critical (CVE-eligible)** | *(documented: [hashing.adoc](./plugin-core/docs/src/docs/passwords/hashing.adoc))* |
| P2 | **Session fixation is prevented by default**: a new HTTP session is created on successful authentication and the previous session's attributes are migrated. | core | [CWE-384](https://cwe.mitre.org/data/definitions/384.html) | `useSessionFixationPrevention: true` (default). | The authenticated user retains the pre-login session ID. | **Security-critical (CVE-eligible)** | *(documented: [sessionFixation.adoc](./plugin-core/docs/src/docs/sessionFixation.adoc))* |
| P3 | **Pessimistic URL coverage**: URLs without an explicit Requestmap / `@Secured` / staticRules rule are denied by default. | core | [CWE-862](https://cwe.mitre.org/data/definitions/862.html) | `rejectIfNoRule: true` (default). | An uncovered URL is reachable by an unauthenticated client. | **Security-critical (CVE-eligible)** | *(documented: [requestMappings.adoc](./plugin-core/docs/src/docs/requestMappings.adoc))* |
| P4 | **`FilterSecurityInterceptor` enforces `@Secured` and Requestmap rules at the HTTP-request boundary** before the controller action runs. | core + compat | [CWE-285](https://cwe.mitre.org/data/definitions/285.html) | The plugin's filter chain is registered (default) and the interceptor's `securityMetadataSource` is configured. | A secured URL handler executes for a caller missing the required authority. | **Security-critical (CVE-eligible)** | *(documented: [requestMappings.adoc](./plugin-core/docs/src/docs/requestMappings.adoc))* |
| P5 | **`MutableAclService` evaluates object-level permissions via `AclPermissionEvaluator`** for `@PreAuthorize("hasPermission(...)")` and `@PostFilter` annotations. | acl | [CWE-285](https://cwe.mitre.org/data/definitions/285.html) | The ACL plugin is installed; AclEntry/AclObjectIdentity rows are populated. | Object-level permission check returns GRANT for a principal without a matching ACE. | **Security-critical (CVE-eligible)** | *(inferred)* |
| P6 | **Persistent remember-me detects token theft**: a series-ID match with a non-matching token value invalidates all of that user's tokens. | core | [CWE-294](https://cwe.mitre.org/data/definitions/294.html) | `rememberMe.persistent: true`. | A stolen-and-reused token continues to authenticate after the legitimate user re-uses theirs. | **Security-critical (CVE-eligible)** | *(inferred)* |
| P7 | **Account-status accessors gate authentication**: `isAccountNonExpired`, `isAccountNonLocked`, `isCredentialsNonExpired`, `isEnabled` each throw a distinct exception. | core | [CWE-287](https://cwe.mitre.org/data/definitions/287.html) | `UserDetails` accessors are wired to GORM `User` fields (default). | A locked / expired / disabled account authenticates successfully. | **Security-critical (CVE-eligible)** | *(documented: [locking.adoc](./plugin-core/docs/src/docs/passwords/locking.adoc))* |
| P8 | **LDAP bind authentication forwards credentials to the LDAP server for verification** rather than fetching the password hash. | ldap | [CWE-522](https://cwe.mitre.org/data/definitions/522.html) | `ldap.authenticator.useBind: true` (default). | Authentication accepts credentials the LDAP server rejected. | **Security-critical (CVE-eligible)** | *(documented: [`DefaultLdapSecurityConfig.groovy`](./plugin-ldap/plugin/grails-app/conf/DefaultLdapSecurityConfig.groovy))* |
| P9 | **CAS ticket validation contacts the CAS server over HTTPS to verify a service ticket** before establishing the local `Authentication`. | cas | [CWE-294](https://cwe.mitre.org/data/definitions/294.html) | `cas.serverUrlPrefix` is HTTPS; JVM trust store accepts the CAS server's certificate. | A forged or attacker-supplied service ticket establishes an authenticated session. | **Security-critical (CVE-eligible)** | *(documented: [`SpringSecurityCasGrailsPlugin.groovy`](./plugin-cas/plugin/src/main/groovy/grails/plugin/springsecurity/cas/SpringSecurityCasGrailsPlugin.groovy))* |
| P10 | **JWT signature is verified before claims are trusted** (HMAC for signed JWT, RSA for encrypted JWT). | rest | [CWE-347](https://cwe.mitre.org/data/definitions/347.html) | `rest.token.storage.jwt.secret` is non-null (HMAC mode) OR a non-null `RSAKeyProvider` is wired (encrypted-JWT mode). **Both being null defeats this property** - `JwtService.parse()` accepts `PlainJWT` (`alg=none`) tokens. | An unsigned (`alg=none`) or invalidly-signed JWT establishes an authenticated session. | **Security-critical (CVE-eligible)** | *(inferred)* (§14 wave 1) |
| P11 | **The REST validation filter checks the JWT `exp` claim against current time** before accepting a token. | rest | [CWE-613](https://cwe.mitre.org/data/definitions/613.html) | `JwtTokenStorageService.loadUserByToken` reaches the `expirationTime` comparison. | An expired JWT continues to authenticate. | **Security-critical (CVE-eligible)** | *(inferred)* |
| P12 | **Channel security redirects HTTP to HTTPS when `secureChannel.definition` marks a URL as `REQUIRES_SECURE_CHANNEL`**. | core + compat | [CWE-319](https://cwe.mitre.org/data/definitions/319.html) | `secureChannel.definition` is configured. **Behind a TLS-terminating proxy, also requires `useHeaderCheckChannelSecurity: true` AND a proxy that sets `X-Forwarded-Proto`**. | A request reaches a `REQUIRES_SECURE_CHANNEL` URL over HTTP without redirect. | **Security-critical (CVE-eligible)** | *(documented: [channelSecurity.adoc](./plugin-core/docs/src/docs/channelSecurity.adoc))* |
| P13 | **`withForm` blocks naive CSRF on UI plugin forms that opt in via `<s2ui:form useToken="true">`**. | ui | [CWE-352](https://cwe.mitre.org/data/definitions/352.html) | Form rendered with `useToken="true"`; the controller's `withForm` block validates the token. | Token validation accepts a missing or attacker-supplied value. | **Security-critical (CVE-eligible)** | *(inferred)* |
| P14 | **Password comparison delegates to the configured `PasswordEncoder.matches()`**, which is constant-time in `BCryptPasswordEncoder`, `Pbkdf2PasswordEncoder`, `Argon2PasswordEncoder`, and `SCryptPasswordEncoder`. | core | [CWE-208](https://cwe.mitre.org/data/definitions/208.html) | `password.algorithm` is one of the listed encoders. The `noop` and message-digest encoders are **not** guaranteed constant-time. | Remote timing oracle distinguishes valid vs invalid credentials. | **Hardening / context-dependent** | *(inferred)* |
| P15 | **Username enumeration via authentication-exception type is suppressed by default** (`hideUserNotFoundExceptions: true`, plus `NoStackUsernameNotFoundException` mapped to `AuthenticationFailureBadCredentialsEvent`). | core | [CWE-204](https://cwe.mitre.org/data/definitions/204.html) | `hideUserNotFoundExceptions: true` (default). | Unknown-username and bad-password failures produce distinguishable responses (status code, error message, or event type). | **Security-critical (CVE-eligible)** | *(inferred)* |

### Resource consumption line

- Bcrypt with `logrounds <= 12` and `password.maxLength <= 72` keeps per-attempt CPU bounded. *(inferred)* (§14 wave 2)
- DoS via large `logrounds` set by the operator is `OUT-OF-MODEL: non-default-build` per §3. *(inferred)*
- Super-linear behavior in user-supplied passwords below the configured `maxLength` is a bug. *(inferred)* (§14 wave 2)

---

## §9 Security properties the plugins do NOT provide

These properties are **disclaimed by design**. A report that depends on one of them is a `BY-DESIGN: property-disclaimed` triage outcome (see §13).

- **CSRF protection on REST/JWT endpoints.** Bearer tokens are the auth credential; there is no synchronizer-token mechanism on `/api/login`, `/api/validate`, or any application endpoint protected by `RestTokenValidationFilter`. The application MUST treat CORS configuration as the front-line CSRF defense for REST APIs. *(inferred)*
- **CSRF protection on forms not protected by `<s2ui:form useToken="true">`.** The plugin ships `useToken` opt-in; the registration form (`register.gsp`) and the password-reset form (`resetPassword.gsp`) do NOT use it. *(inferred)*
- **Anti-bot or rate limiting on `/login`, `/register`, `/register/forgotPassword`, `/api/login`.** No CAPTCHA, no built-in throttling. Operators must add this at the proxy / via Spring Security's `LoginUrlAuthenticationFailureHandler` extensions / via `bucket4j`. *(inferred)*
- **Reset-token and registration-code expiry.** `RegistrationCode` records carry a `dateCreated` field but the `verifyRegistration` and `resetPassword` actions perform no expiry check. A reset token remains valid until consumed or manually deleted. *(inferred)*
- **Account-enumeration resistance on `/register/forgotPassword`.** The action returns a distinguishable response (a field-level error on `forgotPasswordCommand.errors`) for unknown usernames. *(inferred)*
- **Refresh-token rotation and replay detection.** `RestOauthController.accessToken` reuses the supplied refresh token verbatim on the new access-token response. A stolen refresh token can be used an unlimited number of times until its `refreshExpiration` elapses (default `null`, no expiry). *(inferred)*
- **Server-side revocation of stateless JWT.** `JwtTokenStorageService.removeToken()` throws `TokenNotFoundException` unconditionally; the logout endpoint returns HTTP 404 for JWT-backend deployments. **JWT logout is cosmetic.** *(inferred)*
- **JWT claim validation beyond `exp`.** `JwtService` and `JwtTokenStorageService` do NOT validate `iss`, `aud`, `nbf`, `iat`, or `kid`. No issuer allow-list, no audience binding. *(inferred)*
- **JWT algorithm allow-list.** `rest.token.storage.jwt.algorithm` accepts any Nimbus algorithm string. There is no constraint preventing operator configuration of `HS256` and accepting `RS256` or vice versa. *(inferred)*
- **Rejection of `alg=none` JWTs when both `jwtSecret` and `keyProvider` are null.** `JwtService.parse()` accepts `PlainJWT` in this configuration. The plugin will boot in this state when `useSignedJwt: false` AND `useEncryptedJwt: false`. *(inferred)* (§14 wave 1)
- **PKCE for OAuth2 authorization code flow.** `ServiceBuilder.withPkce()` is not invoked. *(inferred)*
- **Cryptographically secure OAuth2 `state` parameter.** The plugin uses `java.util.Random` over a 1,000,000-value space. *(inferred)*
- **`X-Forwarded-For` awareness in `IpAddressFilter`.** Only `request.remoteAddr` is consulted. *(inferred)*
- **`X-Forwarded-Port` awareness in `PortResolverImpl`.** The channel redirect URL inherits `request.serverPort`, which is the backend port behind a TLS-terminating proxy. *(inferred)*
- **CAS server certificate pinning.** TLS trust collapses to the JVM default trust store. *(inferred)*
- **LDAP StartTLS.** The plugin does not wire StartTLS negotiation. *(inferred)*
- **Default TLS for LDAP.** `ldap.context.server` defaults to `ldap://`. *(inferred)*
- **Session fixation prevention when CAS single-logout is enabled.** Setting `cas.useSingleSignout: true` (default for the CAS plugin) **disables** `useSessionFixationPrevention` globally. *(inferred)*
- **Mass-assignment protection in UI domain bindings.** The UI plugin's services use `instanceOrClass.newInstance(data)` / `instance.properties = data` with the raw `params` map. No `bindable: false` is declared on any domain class shipped by the plugin. *(inferred)*
- **Default authorization on UI plugin endpoints.** No `@Secured` annotation on any UI controller; no default Requestmap row. *(inferred)*
- **Password encoding in UI `saveUser` / `updateUser` when `security.ui.encodePassword: false` (default).** The UI service stores submitted passwords without encoding in this default configuration. *(documented: [`DefaultUiSecurityConfig.groovy`](./plugin-ui/plugin/grails-app/conf/DefaultUiSecurityConfig.groovy))*
- **Java deserialization safety for Redis / Memcached token-storage backends.** `RedisTokenStorageService.deserialize` and `CustomSerializingTranscoder` read `ObjectInputStream` from the configured backend. If the backend is reachable by an attacker, this is a deserialization-RCE sink. *(inferred)*
- **Cross-tenant isolation.** One application per JVM is assumed. *(inferred)*
- **Transport security.** Provided by Spring Boot / the operator's proxy / the operator's LDAP/CAS/OAuth2 endpoints. *(inferred)*

### False-friend properties (the highest-value section for integrators)

Features that **look like** a security property but are not one. Reports that confuse a false friend for the real thing are `KNOWN-NON-FINDING` (§11a) when documented and `BY-DESIGN: property-disclaimed` (§13) when not.

- **`@Secured("ROLE_ADMIN")` is not enforced by the annotation itself.** It populates `FilterSecurityInterceptor`'s metadata source; if the interceptor is not in the filter chain (e.g., user-defined `SecurityFilterChain` shadowed it via `ComponentBasedConfigBlender`), the annotation is inert. *(inferred)*
- **`bindable: false` on a UI domain class is not honored by the UI plugin's own save/update flows.** The plugin's `setProperties(params, instance, ...)` calls `instance.properties = params`, which respects `bindable: false` declared on the **domain class** but does NOT add one if the application has not. The plugin ships no `bindable` constraints on `User`, `Role`, `Requestmap`, `Person`, etc. The application must add them. *(inferred)*
- **`useToken="true"` on `<s2ui:form>` is CSRF protection only for the actions that wrap the body in `withForm { } invalidToken { }`.** Forms rendered with `useToken="true"` but POSTing to an action that does not call `withForm` are not protected. The registration form has no `useToken` attribute; the password-reset form has no `useToken` attribute. *(inferred)*
- **`hideUserNotFoundExceptions: true` does not hide enumeration on UI flows.** It only affects the authentication failure event/exception type. The `RegisterController.forgotPassword` action emits a distinguishable response for unknown usernames irrespective of this flag. *(inferred)*
- **`cas.useSingleSignout: true` is a security feature whose implementation requires disabling another security feature.** Operators who enable SLO and rely on session-fixation prevention have neither. *(inferred)*
- **`excludeSpringSecurityAutoConfiguration: false` does not "gracefully fall back" to Boot's defaults.** Both servlet security stacks register their own filter chains. The README documents this as "a footgun" with no precedence guarantee. *(documented: [README.md](./README.md))*
- **`useEncryptedJwt: true` with `DefaultRSAKeyProvider` is not production-ready** despite booting successfully. The provider generates a fresh RSA key pair on each JVM start; tokens are unusable across restarts and across pods. *(inferred)*
- **`rest.token.storage.jwt.algorithm = HS256` is not pinned to HS256-only verification.** Operators reading the doc as "we use HS256" should verify the runtime configuration of `useSignedJwt` / `useEncryptedJwt`; the validation path branches on the JWT type, not on this property. *(inferred)*
- **`ipRestrictions` looks like network-layer protection but operates on `request.remoteAddr`.** Behind a reverse proxy that does not preserve the source IP at the TCP layer, the filter sees only the proxy address. *(inferred)*
- **`secureChannel.useHeaderCheckChannelSecurity: true` makes the channel decision proxy-aware, but `PortResolverImpl` is not** - the redirect URL still uses `request.serverPort`. *(inferred)*
- **`RUN_AS_*` config attributes in the compat shim are not HMAC-protected when constructed.** `RunAsManagerImpl.buildRunAs` does not sign the substituted token; the `key` field is consulted by `RunAsImplAuthenticationProvider` only on the return path. A `RUN_AS_*` attribute injected into the metadata source elevates privilege for the duration of the call. *(inferred)*
- **`GroovyAwareAclVoter` grants `ACCESS_GRANTED` unconditionally for Groovy meta-methods** (`getMetaClass`, `setMetaClass`, `invokeMethod`, `getProperty`, `setProperty`, etc.) on secured objects. This is intentional - Groovy meta-method access must not be ACL-gated - but it means a low-privilege caller can invoke `setMetaClass` on a secured bean regardless of ACL state. *(inferred)*
- **`AffirmativeBased` first-grant-wins semantics mean a permissive voter anywhere in the list overrides every denial.** Operators adding custom voters must ensure they vote `ACCESS_ABSTAIN` rather than `ACCESS_GRANTED` for cases they do not authoritatively handle. *(inferred)*
- **`spring.security.user.name` / `password` / `roles` in production config silently creates a valid credential** with `{noop}` password hashing when `componentBased.bridgeSpringSecurityUserProperties: true` (default). A leftover development convenience becomes a production credential. *(inferred)*
- **`security.ui.encodePassword: false` (the default) stores submitted passwords without hashing** when the UI plugin creates or updates a user. The flag name reads as a behavior toggle but its default is the insecure value. *(inferred)*
- **`AntPathRequestMatcher` does not normalize `../` or URL-encoded path segments** before matching. A request like `/admin%2F..%2Fpublic` may match a `permitAll` rule for `/public/**` while the servlet routes the request to `/admin/...`. Path normalization is expected to have occurred upstream in the servlet container. *(inferred)*

### Well-known attack classes against this category of project that the plugins do not defend against

One sentence per class.

- **Credential stuffing.** No rate-limiting, lockout-after-N-failures, or distributed-attack detection. *(inferred)*
- **Token theft via XSS.** When the application stores REST/JWT tokens in `localStorage`, an XSS in the application leaks the token. The plugins offer no defense - they neither set `HttpOnly` cookies for the token nor restrict its rendering. *(inferred)*
- **OAuth2 authorization-code interception.** PKCE is not used. *(inferred)*
- **OAuth2 state-CSRF.** State is generated from `java.util.Random` over 1M values. *(inferred)*
- **Session-store deserialization gadget chains.** When `SecurityContext` or `SynchronizerTokensHolder` is serialized to a Java-serialization-backed session store, classpath gadgets become reachable. *(inferred)*
- **LDAP referral chasing.** Setting `referral: follow` invites JNDI redirection to an attacker-controlled directory. *(inferred)*
- **CAS proxy ticket abuse.** PGT receptor endpoints trust the CAS server on TLS alone. *(inferred)*
- **Persistent-login table read.** The UI plugin's `PersistentLoginController.search` exposes live remember-me token values to anyone reaching the endpoint. *(inferred)*

---

## §10 Downstream responsibilities

For the assumptions in §5-§7 to hold, the **application developer / operator** must:

1. Set `password.algorithm = bcrypt` (or `pbkdf2` / `argon2` / `scrypt`) and confirm `password.bcrypt.logrounds >= 12` for production. Never use `noop` or message-digest. *(documented: [hashing.adoc](./plugin-core/docs/src/docs/passwords/hashing.adoc))*
2. Set `rememberMe.key` to a strong random string (>=32 random bytes) when `rememberMe.persistent: false`. *(inferred)*
3. Set `cas.key` to a strong random string in production; the default `'grails-spring-security-cas'` is the value shipped in source. *(documented)*
4. For REST/JWT deployments, set `rest.token.storage.jwt.secret` to >=256 random bits, OR configure a non-`DefaultRSAKeyProvider` key source. Never deploy with both null. *(inferred)*
5. Set `rest.token.storage.jwt.refreshExpiration` to a finite value. *(inferred)*
6. Keep `rejectIfNoRule: true` (default) and ensure every URL the application exposes has a Requestmap / `@Secured` / `staticRules` entry. *(documented: [requestMappings.adoc](./plugin-core/docs/src/docs/requestMappings.adoc))*
7. Add explicit Requestmap or `@Secured` protection for every UI plugin endpoint that is installed: `/user/**`, `/role/**`, `/requestmap/**`, `/registrationCode/**`, `/persistentLogin/**`, `/aclClass/**`, `/aclEntry/**`, `/aclObjectIdentity/**`, `/aclSid/**`, `/securityInfo/**`. **The UI plugin ships none.** *(inferred)*
8. Set `security.ui.encodePassword: true` for production. *(documented: [`DefaultUiSecurityConfig.groovy`](./plugin-ui/plugin/grails-app/conf/DefaultUiSecurityConfig.groovy))*
9. Declare `bindable: false` on every privileged field of `User`, `Person`, or equivalent domain class (`accountLocked`, `accountExpired`, `enabled`, `passwordExpired`, `roles`, plus any role-association property). *(inferred)*
10. Validate `security.ui.register.defaultRoleNames` and `oauth2.registration.roleNames` against the set of roles that may be granted to a self-registered user. Never include `ROLE_ADMIN`. *(inferred)*
11. Use HTTPS (`ldaps://` or StartTLS via custom strategy) for LDAP; ensure JVM trust store accepts the CAS and OAuth2 provider certificates; never deploy with HTTP `cas.serverUrlPrefix`. *(inferred)*
12. When behind a TLS-terminating proxy, configure `secureChannel.useHeaderCheckChannelSecurity: true` AND ensure the proxy sets `X-Forwarded-Proto`. Note that channel redirects will still carry the backend port unless the proxy is configured to rewrite the response location. *(documented: [channelSecurity.adoc](./plugin-core/docs/src/docs/channelSecurity.adoc))*
13. When using `ipRestrictions` behind a proxy, configure the proxy to pass-through or rewrite source IPs at the TCP layer; the filter does not honor `X-Forwarded-For`. *(inferred)*
14. Set `oauth2.providers.{name}.debug: false` for production deployments. *(inferred)*
15. Validate `oauth2.frontendCallbackUrl` against an allow-list before redirecting; the plugin appends the access token to the URL as a query parameter. *(inferred)*
16. Add a CAPTCHA or rate-limiter on `/login`, `/api/login`, `/register`, `/register/forgotPassword`. *(inferred)*
17. Disable `rest.login.useRequestParamsCredentials` (the default `false`); never accept credentials in URL query strings. *(documented)*
18. Lock down DB write access to `Requestmap`, `Role`, `AclClass`, `AclEntry`, `AclObjectIdentity`, `AclSid`, and `PersistentLogin` tables. A `permitAll` row in `Requestmap` voids the entire authorization policy. *(inferred)*
19. For Redis / Memcached token-storage backends, place the store on an isolated network and enable AUTH. The token-storage transcoders deserialize Java-serialized payloads without sanitization. *(inferred)*
20. For applications that mount the CAS plugin, accept that session-fixation prevention is disabled while `cas.useSingleSignout: true`; mitigate by enforcing TLS, `HttpOnly` + `Secure` session cookies, and short session timeouts. *(inferred)*
21. Apply `bindable: false` or an explicit `include`/`exclude` on every UI form binding path that touches a privileged domain class. *(inferred)*
22. Never source `application.groovy` or `grails.config.locations` from attacker-writable storage; closures defined there are arbitrary code execution. *(inferred)*
23. Treat `RegistrationCode.token` as a sensitive value: rotate / delete after use, set a manual expiry mechanism (Quartz cron, `lastUsed` cleanup), do not log. *(inferred)*

---

## §11 Known misuse patterns

In-the-wild patterns the API permits but that violate the assumptions in §5-§7.

- **Posting to `/user/save` or `/user/update` with `ROLE_ADMIN=on` as a form parameter.** The UI plugin's `roleNamesFromParams()` collects every key matching `ROLE_*` with value `on` and grants those roles. A low-privilege authenticated user (or unauthenticated client if `/user/save` is unprotected) escalates by setting role checkboxes the UI never rendered. *Fix*: enforce role-grant authorization at the controller layer; render the form server-side with only the roles the current admin is allowed to grant; reject unknown role-named keys. *(inferred)*
- **Posting to `/requestmap/save` with `url='/**', configAttribute='permitAll'`.** Wipes the entire application's authorization policy on the next `clearCachedRequestmaps()` call. *Fix*: protect `/requestmap/**` behind the strongest available authority and a separate change-control workflow; consider gating writes behind an out-of-band approval step. *(inferred)*
- **Posting to `/role/save` to create `ROLE_ADMIN`, then `/user/update` to grant it.** Two-step privilege escalation. *Fix*: same as above; also restrict the set of role names creatable via the UI. *(inferred)*
- **Posting to `/aclEntry/save` to grant ADMINISTRATION on an arbitrary object identity to the current SID.** Direct ACL takeover. *Fix*: protect `/aclEntry/**` with the authority configured for `acl.authority.changeAclDetails`. *(inferred)*
- **Self-registering when `security.ui.register.defaultRoleNames` contains a privileged role.** Every verified-email user becomes an administrator. *Fix*: keep `defaultRoleNames` at `['ROLE_USER']` or equivalent; require manual approval to grant additional roles. *(documented: [`DefaultUiSecurityConfig.groovy`](./plugin-ui/plugin/grails-app/conf/DefaultUiSecurityConfig.groovy))*
- **Deploying with `security.ui.encodePassword: false` (the documented default).** Submitted passwords are stored without encoding. *Fix*: explicitly set `security.ui.encodePassword: true` in production config. *(documented)*
- **Setting `cas.useSingleSignout: true` while expecting session-fixation prevention.** Both cannot be true; SLO disables fixation prevention globally. *Fix*: prefer per-request session-cookie hardening (`HttpOnly`, `Secure`, `SameSite=Lax`), short session timeouts, and authentication-required redirects rather than SLO; OR accept the trade-off and document the residual risk. *(inferred)*
- **Setting `excludeSpringSecurityAutoConfiguration: false` to "preserve Spring Boot's defaults".** Both stacks register filter chains; precedence is undefined. *Fix*: leave the default `true`. *(documented: [README.md](./README.md))*
- **Deploying with `useSignedJwt: false`, `useEncryptedJwt: false`, `jwtSecret: null`, no `RSAKeyProvider`.** `JwtService.parse()` accepts `PlainJWT` (`alg=none`) tokens. **Trivial authentication bypass.** *Fix*: explicitly set `useSignedJwt: true` AND a non-null `jwtSecret` of >=256 random bits; reject the configuration combination above at startup. *(inferred)* (§14 wave 1)
- **Deploying with `DefaultRSAKeyProvider` in production** (ephemeral keys generated per JVM start). Tokens are unusable across pods. *Fix*: configure `FileRSAKeyProvider` with operator-managed DER keys. *(inferred)*
- **Setting `oauth2.{provider}.debug: true` in production.** Raw OAuth traffic, including tokens, is logged. *Fix*: keep `debug: false` outside of development. *(inferred)*
- **Setting LDAP `referral: 'follow'`.** JNDI follows referrals to arbitrary servers. *Fix*: keep `referral: null` (default). *(inferred)*
- **Deploying with default `ldap://localhost:389` URL.** Manager credentials and user passwords transit in cleartext. *Fix*: change to `ldaps://`. *(documented: [`DefaultLdapSecurityConfig.groovy`](./plugin-ldap/plugin/grails-app/conf/DefaultLdapSecurityConfig.groovy))*
- **Reusing the published `cas.key` default `'grails-spring-security-cas'`.** Cross-deployment forgery becomes practical. *Fix*: regenerate per deployment. *(documented)*
- **Putting reset / registration URLs through a TLS-terminating proxy that injects an attacker-controlled `Host` header.** The reset email's link is built from `request.serverName`; an attacker who can manipulate `Host` may receive credential resets. *Fix*: set `grails.serverURL` in production config or have the proxy strip / validate the `Host` header. *(inferred)*
- **Exposing `/securityInfo/config` or `/securityInfo/filterChains` to any non-admin caller.** These endpoints dump the live security configuration including filter ordering, voter list, and bean wiring - a high-value reconnaissance prize. *Fix*: protect `/securityInfo/**` with the strongest available authority. *(inferred)*
- **Allowing the `paramName` query parameter on `AbstractS2UiDomainController.ajaxSearch` to drive a GORM `ilike` criterion property name without validation.** A crafted `paramName` may probe non-search fields or trigger errors that disclose schema. *Fix*: validate against an allow-list before binding. *(inferred)* (§14 wave 3)
- **Storing security questions or answers in plaintext.** The plugin compares answers via `passwordEncoder.matches(submittedAnswer, storedAnswer)`, expecting stored answers to be hash-encoded. If the operator stores answers in cleartext, comparison fails silently; if they pre-hash with a different encoder, the same. *Fix*: hash answers with the same `PasswordEncoder` used for passwords. *(inferred)*
- **Trusting that `useToken="true"` covers all UI plugin forms.** It does not cover `register.gsp` or `resetPassword.gsp`. *Fix*: add `useToken="true"` and wrap the controller body in `withForm` in any future UI plugin form additions; for the registration / reset paths, add per-request CAPTCHA. *(inferred)*

---

## §11a Known non-findings (recurring false positives)

The mirror of §11: patterns that scanners, fuzzers, AI analyzers, or human reviewers repeatedly flag against this project that **are not bugs given the model**. Feed this section to suppression configurations.

- **"`Class.forName(name)` in `GormAclLookupStrategy` line 298"** ([`GormAclLookupStrategy.groovy`](./plugin-acl/plugin/src/main/groovy/grails/plugin/springsecurity/acl/jdbc/GormAclLookupStrategy.groovy)) - SAST flags reflective class loading. The `name` originates from the `AclClass.className` database column, written by the application's own ACL-administration code, not by HTTP request input. Discharged by §6 trust assumption on the ACL tables ("trusted developer/operator input"). → `OUT-OF-MODEL: trusted-input` provided the application's own ACL-administration endpoints are authorized.
- **"`Class.forName(clientClass)` in `RestOauthService` line 62"** ([`RestOauthService.groovy`](./plugin-rest/spring-security-rest/grails-app/services/grails/plugin/springsecurity/rest/RestOauthService.groovy)) and **"`grailsApplication.getClassForName(tokenClassName)` in `GormTokenStorageService`"** ([`GormTokenStorageService.groovy`](./plugin-rest/spring-security-rest-gorm/src/main/groovy/grails/plugin/springsecurity/rest/token/storage/GormTokenStorageService.groovy)) - SAST flags config-driven class loading. Class names come from `application.yml`/`application.groovy`, not from request data. Discharged by §6. → `OUT-OF-MODEL: trusted-input`.
- **"`GroovyClassLoader.loadClass(className)` in `SpringSecurityUtils.mergeConfig`"** - loads named configuration classes (e.g. `DefaultSecurityConfig`, secondary-plugin defaults). Class names are hardcoded in plugin descriptors. → `OUT-OF-MODEL: trusted-input`.
- **"Groovy `Closure` execution in `SecurityEventListener` / `ClosureVoter`"** - closures are defined in `application.groovy` at deploy time. Discharged by §6 trust assumption on `application.groovy`. → `OUT-OF-MODEL: trusted-input` (or `BY-DESIGN: property-disclaimed` if the assumption is violated).
- **"Dynamic property access `user.\"$propertyName\"` in `GormUserDetailsService`"** - property names come from `securityConfig.userLookup.*PropertyName`, a trusted config object. → `OUT-OF-MODEL: trusted-input`.
- **"`@PreAuthorize` / `@PostFilter` SpEL evaluation with `StandardEvaluationContext`"** ([`ExpressionBasedPreInvocationAdvice.groovy`](./spring-security-compat/src/main/groovy/org/springframework/security/access/expression/method/ExpressionBasedPreInvocationAdvice.groovy)) - SAST flags SpEL with `T(...)` type references reachable. Annotation text is compiled into the bytecode; it is not constructed from HTTP request input anywhere in the plugins. Discharged by §6 trust assumption on annotation source. → `OUT-OF-MODEL: trusted-input` provided no application code path feeds user input into the annotation string.
- **"`AffirmativeBased` first-grant-wins decision semantics"** - documented Spring Security 5.x behavior, vendored into `spring-security-compat`. → `KNOWN-NON-FINDING` (architectural decision, not a bug).
- **"`GroovyAwareAclVoter` grants `ACCESS_GRANTED` unconditionally for Groovy meta-methods"** ([`GroovyAwareAclVoter.groovy`](./plugin-acl/plugin/src/main/groovy/grails/plugin/springsecurity/acl/access/GroovyAwareAclVoter.groovy)) - intentional. Groovy meta-method access must not be ACL-gated; doing so would break method dispatch on every secured bean. → `KNOWN-NON-FINDING`.
- **"`AntPathRequestMatcher` does not normalize `../` or URL-encoded segments"** ([`AntPathRequestMatcher.groovy`](./spring-security-compat/src/main/groovy/org/springframework/security/web/util/matcher/AntPathRequestMatcher.groovy)) - documented Spring Security 5.x behavior. Normalization is the servlet container's responsibility. → `OUT-OF-MODEL: unsupported-component` for the container's normalization posture; the application must rely on Tomcat / Jetty defaults or add an explicit normalization filter.
- **"Java deserialization in `JwtService.deserialize`"** ([`JwtService.groovy`](./plugin-rest/spring-security-rest/grails-app/services/grails/plugin/springsecurity/rest/JwtService.groovy)) - the deserialized payload was serialized by the same application at token-generation time and is protected by the JWT signature. Discharged by §8 P10 (signature verified before claims are trusted). **In-model only if** the `alg=none` path is reachable (see §9 disclaimer and §11 misuse); otherwise → `KNOWN-NON-FINDING`.
- **"Java deserialization in `RedisTokenStorageService.deserialize` and `CustomSerializingTranscoder` (Memcached)"** - discharged by §5 environment assumption that the Redis / Memcached backend is operator-controlled and network-isolated. **In-model only if** the backend is reachable by an untrusted client (the operator's network posture); → `OUT-OF-MODEL: trusted-input` for the framework, escalating to `VALID` if the operator's network is not isolated.
- **"`Serializable` classes in plugin source"** (`PersistentLogin`, `RegistrationCode`, `User` / `Person` / `Role` domain classes) - the domain classes implement `Serializable` for GORM's optimistic-locking / session-serialization needs. Discharged by §6 trust assumption on the session store. → `OUT-OF-MODEL: trusted-input`.
- **"`@Secured("ROLE_X")` present but no in-method authz logic"** - identical to the framework's posture. The annotation is enforced by `FilterSecurityInterceptor`, not inline. → `KNOWN-NON-FINDING`.
- **"`MutableRoleHierarchy.setHierarchy(String)` mutates the role hierarchy at runtime"** ([`MutableRoleHierarchy.groovy`](./plugin-core/plugin/src/main/groovy/grails/plugin/springsecurity/MutableRoleHierarchy.groovy)) - SAST flags a privilege-escalation surface. The hierarchy string is loaded from `RoleHierarchyEntry` DB rows at startup. Discharged by §6 trust assumption on the role hierarchy table. → `OUT-OF-MODEL: trusted-input` for the framework; the application's authorization on writes to `RoleHierarchyEntry` is its own responsibility (see §10 #18).
- **"`InsecureChannelProcessor` actively redirects HTTPS to HTTP"** ([`InsecureChannelProcessor.groovy`](./spring-security-compat/src/main/groovy/org/springframework/security/web/access/channel/InsecureChannelProcessor.groovy)) - the processor is invoked only on URLs explicitly marked `REQUIRES_INSECURE_CHANNEL` in `secureChannel.definition`. Operator-opted-in by design. → `KNOWN-NON-FINDING` provided no `REQUIRES_INSECURE_CHANNEL` rule covers a sensitive URL.
- **"Cleartext Memcached / Redis protocol traffic"** - the plugin transmits serialized `UserDetails` over the cache protocol. Discharged by §5 environment assumption. → `OUT-OF-MODEL: trusted-input`.
- **"`MutableLogoutFilter` allows post-logout redirect via `targetUrlParameter` without origin validation"** ([`MutableLogoutFilter.groovy`](./plugin-core/plugin/src/main/groovy/grails/plugin/springsecurity/web/authentication/logout/MutableLogoutFilter.groovy)) - by default `targetUrlParameter` is null and the redirect goes to `defaultTargetUrl: '/'`. Open-redirect only manifests when the operator sets `logout.targetUrlParameter` and does not validate the value at the application layer. → `OUT-OF-MODEL: non-default-build`.

---

## §12 Conditions that would change this model

Revise this document on:

- Addition of a new HTTP endpoint family (e.g., a new admin controller in `plugin-ui`).
- Promotion of any UI controller to default-protected (e.g., shipping default Requestmap rows in plugin bootstrap).
- Migration of `spring-security-compat` off the vendored Spring Security 5.x classes onto Spring Security 6/7 native authorization API.
- Adding PKCE support to `plugin-oauth2`.
- Replacing `java.util.Random` with `SecureRandom` for the OAuth2 `state` parameter.
- Adding an algorithm allow-list to `JwtService.parse()` and rejecting `PlainJWT` (`alg=none`) unconditionally.
- Adding `X-Forwarded-For` / `X-Forwarded-Port` awareness to `IpAddressFilter` and `PortResolverImpl`.
- Changing any §5a default that affects a §8 property (notably `password.algorithm`, `useSessionFixationPrevention`, `rejectIfNoRule`, `security.ui.encodePassword`).
- Introduction of a built-in CSRF subsystem for REST/JWT (would move items from §9 into §8).
- A vulnerability report that **cannot** be cleanly routed to one of the §13 dispositions - that is a `MODEL-GAP` and indicates this document is incomplete. Revise rather than ad-hoc the call.

---

## §13 Triage dispositions

Every report against the plugins receives **exactly one** of the following dispositions. Each cites the section that licenses it. A finding that does not fit is `MODEL-GAP` and triggers a §12 revision, not an ad-hoc judgment.

| Disposition | Meaning | Licensed by |
|---|---|---|
| `VALID` | Violates a property the plugins claim, via an in-scope adversary and input. | §8, §6, §7 |
| `VALID-HARDENING` | No §8 property is violated, but the API makes a §11 misuse easy enough that the project elects to harden it. Reported privately per [SECURITY.md](./SECURITY.md); fixed at maintainer discretion; typically no CVE. | §11 |
| `OUT-OF-MODEL: trusted-input` | Requires attacker control of an input the model marks trusted (classpath, `application.groovy`, `grails.config.locations`, the ACL / Requestmap / Role / RoleHierarchyEntry tables, LDAP manager credentials, JWT secret, CAS key, OAuth2 client secret, Redis / Memcached cache contents, AST transform inputs). | §6 |
| `OUT-OF-MODEL: adversary-not-in-scope` | Requires an attacker capability the model excludes (local shell, JVM co-tenant, side channel, compromised plugin, MITM on operator-trusted TLS channels). | §7 |
| `OUT-OF-MODEL: unsupported-component` | Lands in `plugin-*/examples/`, `spring-security-rest-testapp-profile`, or a third-party plugin. | §3 |
| `OUT-OF-MODEL: non-default-build` | Only manifests under a discouraged or non-default §5a configuration (e.g., `password.algorithm = noop`, `useSessionFixationPrevention: false`, `rejectIfNoRule: false`, `security.ui.encodePassword: false`, `oauth2.{provider}.debug: true`, `ldap://` URL, `ldap.referral: follow`, `cas.useSingleSignout: true`, `excludeSpringSecurityAutoConfiguration: false`, `useSignedJwt: false` AND `useEncryptedJwt: false` AND null secret). | §5a |
| `BY-DESIGN: property-disclaimed` | Concerns a property the plugins explicitly do not provide (CSRF on REST, anti-bot, reset-token expiry, refresh-token rotation, JWT revocation, PKCE, secure state, `X-Forwarded-*` awareness, default UI authorization). | §9 |
| `KNOWN-NON-FINDING` | Matches a documented recurring false positive. | §11a |
| `MODEL-GAP` | Cannot be cleanly routed to any of the above. | triggers §12 |

---

## §14 Open questions for the maintainers

The model is **draft-first**. The questions below are grouped in waves of 3-7 per the Apache security-team rubric. Each is framed as a proposed answer for the PMC to confirm, correct, or strike. Once answered, promote the matching *(inferred)* tags to *(maintainer)* and delete the question.

### Wave 1 - scope and intended use (most-load-bearing answers; §2-§3 depend on these)

1. **Caller-role split.** *Proposed*: the five roles in §2 (unauthenticated HTTP, authenticated low-privilege, developer/operator, plugin/profile author, external IdP) are the correct primitives. Correct or extend? *Lands in §2.*
2. **UI plugin endpoints ship unprotected by design.** *Proposed*: it is the operator's responsibility to add Requestmap / `@Secured` / `staticRules` entries for every controller installed by `plugin-ui`. The plugin will not ship default protection. Confirm. *Lands in §2, §7, §10, §13.*
3. **`jwtSecret` null + `keyProvider` null + `useSignedJwt: false` + `useEncryptedJwt: false` is a footgun, not a vulnerability.** *Proposed*: triagers close such reports as `OUT-OF-MODEL: non-default-build`; the project may also choose to add a startup-time rejection (see §12). Confirm the disposition. *Lands in §5a, §11, §13.*
4. **`cas.useSingleSignout: true` disabling session-fixation prevention is the supported behavior.** *Proposed*: documented in the SLO/SF trade-off; reports against this are `BY-DESIGN: property-disclaimed`. Confirm. *Lands in §5a, §9, §13.*
5. **`security.ui.encodePassword: false` (the default) is acceptable for an out-of-the-box plugin.** *Proposed*: the operator MUST flip it for production. Should the default be flipped to `true`? *Lands in §5a, §10, §12.*
6. **Default `password.bcrypt.logrounds: 10` is the supported production default.** *Proposed*: confirm or raise. The docs note the default is "lower for testing speed" - is the testing default also the production default? *Lands in §5a, §10, §14.*
7. **`spring.security.user.name` bridging is intentional.** *Proposed*: `componentBased.bridgeSpringSecurityUserProperties: true` (default) is the supported behavior despite the risk of leftover dev credentials reaching production. Confirm. *Lands in §5a, §11.*

### Wave 2 - trust boundaries and configuration semantics (§4-§6, §9)

8. **`IpAddressFilter` does not consult `X-Forwarded-For` by design.** *Proposed*: operators behind reverse proxies must configure the proxy to preserve source IPs at the TCP layer. Reports that IP restrictions are bypassable behind a proxy are `BY-DESIGN: property-disclaimed`. Confirm. *Lands in §9, §13.*
9. **`PortResolverImpl` does not consult `X-Forwarded-Port`.** *Proposed*: same disposition as #8. Confirm. *Lands in §9.*
10. **OAuth2 state generated by `java.util.Random` is a known limitation.** *Proposed*: this is `VALID-HARDENING`; the fix is to migrate to `SecureRandom` and is targeted for a future release. Confirm severity and target. *Lands in §9, §11.*
11. **PKCE is not configured in `plugin-oauth2`.** *Proposed*: same disposition as #10. Confirm. *Lands in §9.*
12. **Refresh-token reuse (no rotation) is the supported behavior.** *Proposed*: operators who need rotation can set `refreshExpiration` to a low value and implement application-level rotation. Confirm. *Lands in §9.*
13. **JWT logout being a no-op for stateless JWT is the supported behavior.** *Proposed*: operators who need revocation must use a stateful storage backend (GORM, Redis, Memcached, Grails Cache). Confirm. *Lands in §9, §10.*
14. **JwtService accepts any Nimbus-supported algorithm string** (no per-deployment algorithm allow-list). *Proposed*: this is a known limitation; the fix is to add an allow-list pinned to the deployment's configured algorithm. Confirm and prioritize. *Lands in §9, §12.*

### Wave 3 - misuse and §11a curation

15. **`RUN_AS_*` in the compat shim does not HMAC-sign the substituted token.** *Proposed*: this matches Spring Security 5.x semantics; reports are `KNOWN-NON-FINDING` unless `useRunAs: true` AND the `RUN_AS_*` config attribute is injectable. Confirm. *Lands in §9 false-friend, §11a.*
16. **`AclClass.className` is operator-trusted.** *Proposed*: write access to that column is equivalent to arbitrary classloading; this is a §6 trusted-input assumption, not a vulnerability. Confirm. *Lands in §6, §11a.*
17. **GORM serialization of `User` / `Person` / `Role` for HTTP session is the application's responsibility, not the plugin's.** *Proposed*: the plugin's domain classes implement `Serializable` for GORM; if the session store deserializes from an attacker-reachable channel, the application's choice of session backend is the in-model surface. Confirm. *Lands in §11a.*
18. **`AbstractS2UiDomainController.ajaxSearch` accepts `paramName` and uses it as a GORM property name** without validation. *Proposed*: this is `VALID-HARDENING`; add allow-list validation. Confirm and target a release. *Lands in §11, §12.*
19. **The §13 disposition table is closed and complete.** *Proposed*: no additional plugin-specific disposition is needed beyond the rubric set. Confirm. *Lands in §13.*

### Meta

20. **Document ownership.** *Proposed*: this file lives at the repo root, maintained by the PMC, revised per the §12 triggers. The next release branch should fork this document with its own version binding.
21. **Coexistence with `SECURITY.md`.** *Proposed*: `SECURITY.md` remains the disclosure-process artifact; this file is the model. `SECURITY.md` should add a single line cross-referencing this document.
22. **Coexistence with per-plugin `docs/src/docs/`.** *Proposed*: those documents remain end-user-facing prose; this document is triage-facing. Where they overlap, this document cites the per-plugin doc as the *(documented)* source rather than re-stating prose.

---

## §15 Machine-readable companion

A YAML sidecar at [`threat-model.yaml`](./threat-model.yaml) carries the triage-relevant facts in structured form, regenerated whenever this prose document changes. The prose document is canonical; the YAML is a derived index for automated triage tooling.

---

## Appendix A - back-map: existing documentation → threat-model section

This back-map proves coverage. Every threat-model-shaped claim already in the repository's own documentation is reflected somewhere in this document.

| Existing claim (file:line) | Original wording (paraphrase) | This document |
|---|---|---|
| [`plugin-core/docs/src/docs/passwords/hashing.adoc`](./plugin-core/docs/src/docs/passwords/hashing.adoc) | "By default the plugin uses the bcrypt algorithm to hash passwords." | §8 P1, §5a `password.*` knobs, §10 #1 |
| [`plugin-core/docs/src/docs/passwords/salt.adoc`](./plugin-core/docs/src/docs/passwords/salt.adoc) | "If you use bcrypt or pbkdf2, do not configure a salt - these algorithms use their own internally." | §8 P1, §11 misuse |
| [`plugin-core/docs/src/docs/passwords/locking.adoc`](./plugin-core/docs/src/docs/passwords/locking.adoc) | "`isAccountNonExpired`, `isAccountNonLocked`, `isCredentialsNonExpired`, `isEnabled` accessors gate authentication." | §8 P7 |
| [`plugin-core/docs/src/docs/sessionFixation.adoc`](./plugin-core/docs/src/docs/sessionFixation.adoc) | "Set `useSessionFixationPrevention` to `true` to prevent session-fixation attacks." | §8 P2, §5a, §11 |
| [`plugin-core/docs/src/docs/channelSecurity.adoc`](./plugin-core/docs/src/docs/channelSecurity.adoc) | "`secureChannel.definition` map of URL pattern to channel rule." | §8 P12, §5a, §9 false-friend on `useHeaderCheckChannelSecurity`, §10 #12 |
| [`plugin-core/docs/src/docs/requestMappings.adoc`](./plugin-core/docs/src/docs/requestMappings.adoc) | "Pessimistic lockdown is the default - `rejectIfNoRule: true`." | §8 P3, §5a, §10 #6 |
| [`plugin-core/docs/src/docs/voters.adoc`](./plugin-core/docs/src/docs/voters.adoc) | "Default voters: `authenticatedVoter`, `roleVoter`, `webExpressionVoter`, `closureVoter`." | §4 flow A, §8 P4, §9 false-friend on `AffirmativeBased` |
| [`plugin-rest/docs/src/docs/tokenStorage.adoc`](./plugin-rest/docs/src/docs/tokenStorage.adoc) | "Default JWT signing is HMAC SHA-256 with a shared secret." | §8 P10, §5a, §9 disclaimer on `alg=none` |
| [`plugin-rest/docs/src/docs/tokenValidation.adoc`](./plugin-rest/docs/src/docs/tokenValidation.adoc) | "RFC 6750 Bearer token; default validation looks for the token in `Authorization`." | §6 inputs, §8 P10, §8 P11 |
| [`plugin-rest/docs/src/docs/tokenStorage.adoc`](./plugin-rest/docs/src/docs/tokenStorage.adoc) | "Refresh tokens never expire by default - section 10.4 of RFC 6749 reminds you to store them securely." | §9 disclaimer, §10 #5 |
| [`plugin-cas/docs/src/docs/configuration.adoc`](./plugin-cas/docs/src/docs/configuration.adoc) | CAS configuration properties, including `useSingleSignout`. | §5a, §9 false-friend |
| [`plugin-ldap/docs/src/docs/configuration.adoc`](./plugin-ldap/docs/src/docs/configuration.adoc) | LDAP configuration, including default `ldap://`. | §5a, §9 disclaimer |
| [`plugin-oauth2/docs/src/docs/configuration.adoc`](./plugin-oauth2/docs/src/docs/configuration.adoc) | OAuth2 configuration properties. | §5a, §9 disclaimer |
| [`plugin-ui/docs/src/docs/`](./plugin-ui/docs/src/docs/) | UI plugin scripts and forms. | §2 (UI as the only HTTP-active plugin), §11, §10 #7 |
| [`README.md`](./README.md) | "The plugin automatically excludes Spring Boot's servlet security auto-configuration." | §5a `excludeSpringSecurityAutoConfiguration`, §9 false-friend |
| [`README.md`](./README.md) | Component-based config blending: `autoMergeSecurityFilterChain`, `autoMergeAuthenticationProviders`, `autoChainUserDetailsServices`, `bridgeSpringSecurityUserProperties`. | §5a core knobs, §11 misuse |

No claim in the existing documentation is dropped, weakened, or contradicted by this document. Where the existing documentation and this document would conflict, the documentation wins; raise a §14 question rather than silently editing.
