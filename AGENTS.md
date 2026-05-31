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

# Agent Guide for grails-spring-security

> **IMPORTANT**: This is the source repository for the Apache Grails Spring Security **plugins** (a multi-module Gradle monorepo), NOT a Grails application. The plugins integrate [Spring Security](https://spring.io/projects/spring-security) into Grails® framework applications through the `grails.plugin.springsecurity.*` configuration namespace.
>
> For building Grails **applications** that consume these plugins, follow the upstream Grails framework guidance in [grails-core `AGENTS.md`](https://github.com/apache/grails-core/blob/7.0.x/AGENTS.md) and the [Grails documentation](https://grails.apache.org/).

## Quick Reference

```bash
# First time only: bootstrap the build (publishes the build's own conventions)
cd gradle-bootstrap && gradle && cd -

# Build everything (no tests)
./gradlew build -PskipTests

# Build everything (with tests)
./gradlew build

# Build a single module (logical project name from settings.gradle)
./gradlew :core-plugin:build

# Run a module's tests
./gradlew :core-plugin:test

# Run a single test class / feature
./gradlew :core-plugin:test --tests "grails.plugin.springsecurity.SpringSecurityUtilsSpec"
./gradlew :core-plugin:test --tests "grails.plugin.springsecurity.SpringSecurityUtilsSpec.some feature"

# Publish all plugins to mavenLocal for consumption by a test app
./gradlew publishToMavenLocal

# Apache license audit (RAT) - must pass in CI
./gradlew rat
```

## Critical Rules

1. **Use `jakarta.*` NOT `javax.*`** - Grails 8 / Spring Boot 4 are on Jakarta EE.
2. **Use `@GrailsCompileStatic`** in Grails artefact classes (controllers, services, taglibs); use `@CompileStatic` for plain Groovy classes where dynamic behaviour is not required.
3. **Apache license header on every new source file** - enforced by Apache RAT (`./gradlew rat`). New root markdown files also need the header (see the top of this file, `README.md`, and `SECURITY.md` for the accepted HTML-comment form). RAT exclusions live in `gradle/rat-root-config.gradle`.
4. **No wildcard imports** - use explicit imports.
5. **`grails.plugin.springsecurity.*` is the authoritative configuration source** - not Spring Boot's `spring.security.*`. The plugin auto-excludes Boot's servlet security auto-configuration; see the "Spring Boot Auto-Configuration" section of `README.md` for the full contract and the component-based blending behaviour.
6. **Test through public APIs** - exercise behaviour the same way a plugin consumer would; do not reach into internal/package-private implementation details.
7. **Review and extend tests** - every behavioural change must add or update Spock specs covering the affected code.
8. **User-facing changes need docs** - update the relevant module's `docs/` (Asciidoctor) for any user-visible behaviour or configuration change.
9. **Match the surrounding style** - follow the indentation and conventions of the file/module you are editing (`.gradle` files use tabs; most Groovy sources use 4 spaces). Do not add section-separator/grouping comments.

## Technology Stack

| Component | Version | Source |
|-----------|---------|--------|
| Grails | 8.0.0-M1 | `gradle.properties` (`grailsVersion`) |
| JDK | 21 | `gradle.properties` (`javaVersion`), CI uses Liberica 21 |
| Spring Boot | 4.x | via `grails-bom` |
| Spring Security | 7.x | via `grails-bom` |
| Spock | bundled with Grails | via `grails-bom` |
| Build | Gradle wrapper (`./gradlew`) | `gradlew` |

Library versions are governed by `implementation platform("org.apache.grails:grails-bom:$grailsVersion")`; pinned tool/dependency versions live in `gradle.properties`. This branch (`8.0.x`) builds `projectVersion=8.0.0-SNAPSHOT`.

## Repository Structure

This is a monorepo of independent plugins. Each plugin follows the same layout: `plugin-<name>/plugin` (the publishable plugin), `plugin-<name>/docs` (Asciidoctor docs), and `plugin-<name>/examples` (functional/integration test apps). Logical Gradle project names are mapped to physical directories in `settings.gradle`.

| Logical project | Directory | Purpose |
|-----------------|-----------|---------|
| `core-plugin` | `plugin-core/plugin` | Core plugin: security filter chain, authentication providers, GORM `UserDetailsService`, `@Secured` annotations, taglibs, login/logout controllers |
| `acl-plugin` | `plugin-acl/plugin` | Domain object ACL (access control list) support |
| `cas-plugin` | `plugin-cas/plugin` | CAS single sign-on integration |
| `ldap-plugin` | `plugin-ldap/plugin` | LDAP / Active Directory authentication |
| `oauth2-plugin` | `plugin-oauth2/plugin` | OAuth2 authentication |
| `spring-security-rest` (+ `-gorm`, `-grailscache`, `-memcached`, `-redis`, `-testapp-profile`) | `plugin-rest/*` | Stateless / token-based REST authentication and its token-storage backends |
| `ui-plugin` | `plugin-ui/plugin` | Scaffolding UI: login, user/role registration, ACL management screens |
| `spring-security-compat` | `spring-security-compat` | Compatibility layer for Spring Security API changes |
| `*-docs`, `docs` | `plugin-*/docs`, `docs` | Per-plugin and aggregate documentation |
| `*-examples-*` | `plugin-*/examples/*` | Example apps used as functional/integration test fixtures |

When working on one plugin, prefer its logical project name with Gradle (e.g. `./gradlew :ldap-plugin:test`).

## Core Plugin Layout (`plugin-core/plugin`)

Grails artefacts under `grails-app/`:

- `services/grails/plugin/springsecurity/SpringSecurityService.groovy` - primary service
- `taglib/grails/plugin/springsecurity/SecurityTagLib.groovy` - security tags
- `controllers/grails/plugin/springsecurity/{Login,Logout}Controller.groovy`
- `conf/DefaultSecurityConfig.groovy` - default `grails.plugin.springsecurity.*` values
- `commands/grails/plugin/springsecurity/*.groovy` - `s2-*` command implementations
- `i18n/` and `views/` - messages and login/denied GSPs

Framework and integration code under `src/main/`:

- `groovy/grails/plugin/springsecurity/**` - core classes by concern: `access/`, `annotation/`, `authentication/`, `cache/`, `componentbased/`, `userdetails/`, `web/` (with `web/access`, `web/authentication`, `web/filter`). The plugin descriptor is `SpringSecurityCoreGrailsPlugin.groovy`.
- `templates/*.groovy.template` - domain class templates that `s2-quickstart` generates into the consuming application (RAT-excluded; do not add license headers to these)
- `scripts/s2-*.groovy` - CLI generation scripts
- `resources/META-INF/` - `spring.factories` and `spring-configuration-metadata.json`

> Sources are overwhelmingly Groovy. A small number of Java files exist where annotations or enums require it (e.g. `src/main/groovy/grails/plugin/springsecurity/SecurityFilterPosition.java`).

## Testing

Tests are [Spock](https://spockframework.org/) specifications using Grails testing-support traits (e.g. `GrailsUnitTest`, `ServiceUnitTest`, `DomainUnitTest`). Unit specs live in `src/test/groovy`; integration specs in `src/integration-test/groovy`.

```groovy
class SomeServiceSpec extends Specification implements ServiceUnitTest<SomeService> {
    void "it does the thing"() {
        given: "preconditions"
        def input = 'value'

        when: "the action runs"
        def result = service.doThing(input)

        then: "the outcome holds"
        result
    }
}
```

Functional tests run against the example apps and are exercised in CI under multiple security configurations:

```bash
./gradlew core-examples-functional-test-app:check -DTESTCONFIG=static -PgebAtCheckWaiting
```

CI iterates `TESTCONFIG` over: `static`, `annotation`, `requestmap`, `basic`, `basicCacheUsers`, `misc`, `putWithParams`, `bcrypt`, `issue503`.

## CI and Gates

- **`.github/workflows/gradle.yml`** - `coreTests` runs `./gradlew check --max-workers=2 --refresh-dependencies --continue -PgebAtCheckWaiting`; `functionalTests` runs the `TESTCONFIG` matrix above. Both must pass before the `publish` job runs. JDK 21 (Liberica).
- **`.github/workflows/rat.yml`** - Apache Release Audit Tool (`./gradlew rat`); fails the build on any source file missing an acceptable license header.
- Add `[skip tests]` to a commit message to skip the test jobs.
- CI triggers on pushes and pull requests targeting release branches matching `[3-9]+.[0-9]+.x`.

## Branch Structure

| Branch | Compatible with |
|--------|-----------------|
| `8.0.x` | Grails 8 / Spring Boot 4 / Spring Security 7 |
| `7.0.x` | Grails 7 / Spring Boot 3 / Spring Security 6 |
| `6.0.x` | Grails 6 |
| `5.0.x` and earlier | Grails 5 and earlier |

## Pull Request Guidelines

1. **Branch from the target release branch** (e.g. `8.0.x` for Grails 8 work, `7.0.x` for Grails 7).
2. **Build and audit locally before submitting**: `./gradlew build` and `./gradlew rat`.
3. **Keep Apache license headers** on every new source file.
4. **Add or update Spock tests** for behavioural changes and **docs** for user-facing changes.
5. **Reference issues** in the PR description (e.g. "Fixes #1234"); ensure CI (`coreTests`, `functionalTests`, RAT) is green.

## Security

- Do **not** open public issues, discussions, or PRs for suspected vulnerabilities. Email the ASF Security Team at [security@apache.org](mailto:security@apache.org) with `grails-spring-security` in the subject.
- See [`SECURITY.md`](./SECURITY.md) for the reporting process and supported versions, and [`THREAT_MODEL.md`](./THREAT_MODEL.md) for the authoritative scope of what the plugins do and do not defend against.

## Resources

- **Documentation site**: https://apache.github.io/grails-spring-security/
- **Grails framework**: https://grails.apache.org/
- **Spring Security**: https://spring.io/projects/spring-security
- **Issues**: https://github.com/apache/grails-spring-security/issues
- **Slack**: https://grails.slack.com
