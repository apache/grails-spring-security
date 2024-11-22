[![Java CI](https://github.com/grails/grails-spring-security-core/actions/workflows/gradle.yml/badge.svg)](https://github.com/grails/grails-spring-security-core/actions/workflows/gradle.yml)

Grails Spring Security Core Plugin
==================================

See [documentation](https://grails-plugins.github.io/grails-spring-security-core/) for further information.

### Branch structure 

- `7.0.x` compatible with Grails 7
- `6.0.x` compatible with Grails 6
- `5.0.x` compatible with Grails 5
- `4.0.x` compatible with Grails 4
- `3.3.x` compatible with Grails 3.3.x
- `3.2.x` compatible with Grails 3.2.x

Grails 7 requires disabling any Spring Security Auto Configurations you may have in your classpath.  This can be done via annotation or `application.yml`
e.g.
```yml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration
      - org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
      - org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
      - org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
      - org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration
      - org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration
      - org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
```
