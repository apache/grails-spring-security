/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package grails.plugin.springsecurity

import groovy.transform.CompileStatic

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment

/**
 * Automatically excludes Spring Boot security auto-configuration classes that
 * conflict with the Grails Spring Security plugin.
 *
 * <p>When the Grails Spring Security plugin is on the classpath together with
 * one of Spring Boot's split-out security modules (such as
 * {@code spring-boot-security}, {@code spring-boot-starter-security} or
 * {@code spring-boot-security-oauth2-client}), Spring Boot's security
 * auto-configurations create duplicate {@code SecurityFilterChain} beans and
 * other security infrastructure that conflicts with the plugin's own bean
 * definitions in
 * {@link SpringSecurityCoreGrailsPlugin#doWithSpring}.</p>
 *
 * <p>In Spring Boot 4 the security auto-configurations were moved out of
 * {@code spring-boot-autoconfigure} into dedicated {@code spring-boot-security*}
 * modules and re-packaged under {@code org.springframework.boot.security.*}.
 * Adding any of those starters/modules to a Grails application would
 * re-introduce the conflicting servlet auto-configurations. This filter prevents
 * that by excluding them during Spring Boot's auto-configuration discovery
 * phase.</p>
 *
 * <p>Previously, users had to manually exclude up to 7 auto-configuration classes
 * in {@code application.yml}. This filter removes that requirement by
 * automatically filtering them out during Spring Boot's auto-configuration
 * discovery phase.</p>
 *
 * <p>To disable this filter and allow Spring Boot's security auto-configurations
 * to run, set the following property in {@code application.yml}:</p>
 *
 * <pre>
 * grails:
 *   plugin:
 *     springsecurity:
 *       excludeSpringSecurityAutoConfiguration: false
 * </pre>
 *
 * <p>Registered via {@code META-INF/spring.factories} as an
 * {@link AutoConfigurationImportFilter}. This runs before auto-configuration
 * bytecode is loaded, so there is no performance overhead from excluded classes.</p>
 *
 * @since 8.0.0
 * @see AutoConfigurationImportFilter
 */
@CompileStatic
class SecurityAutoConfigurationExcluder implements AutoConfigurationImportFilter, EnvironmentAware {

    static final String ENABLED_PROPERTY = 'grails.plugin.springsecurity.excludeSpringSecurityAutoConfiguration'

    private boolean enabled = true

    @Override
    void setEnvironment(Environment environment) {
        this.enabled = environment.getProperty(ENABLED_PROPERTY, Boolean, true)
    }

    /**
     * Spring Boot 4 security auto-configuration classes that conflict with the
     * Grails Spring Security plugin's servlet-based security stack. These are
     * excluded unconditionally when the plugin is on the classpath.
     *
     * <p>Verified against the {@code AutoConfiguration.imports} files in the
     * Spring Boot 4 {@code spring-boot-security} and
     * {@code spring-boot-security-oauth2-client} modules.</p>
     *
     * <ul>
     *   <li>{@code SecurityAutoConfiguration} - creates a default
     *       {@code SecurityFilterChain} that conflicts with the plugin's
     *       {@code FilterChainProxy}</li>
     *   <li>{@code UserDetailsServiceAutoConfiguration} - creates an in-memory
     *       {@code UserDetailsService} from {@code spring.security.user.*}
     *       properties that conflicts with the plugin's
     *       {@code GormUserDetailsService}</li>
     *   <li>{@code SecurityFilterAutoConfiguration} - registers a
     *       {@code DelegatingFilterProxyRegistrationBean} that duplicates the
     *       plugin's {@code springSecurityFilterChainRegistrationBean}</li>
     *   <li>{@code ServletWebSecurityAutoConfiguration} - new in Spring Boot 4;
     *       contributes the servlet {@code SecurityFilterChain} wiring that
     *       conflicts with the plugin's filter chain</li>
     *   <li>{@code ManagementWebSecurityAutoConfiguration} - Actuator security
     *       that conflicts when Actuator is on the classpath</li>
     *   <li>{@code OAuth2ClientAutoConfiguration} - registers the
     *       {@code ClientRegistrationRepository} and authorized-client services
     *       that the {@code spring-security-oauth2} plugin owns</li>
     *   <li>{@code OAuth2ClientWebSecurityAutoConfiguration} - registers the
     *       OAuth2 client servlet {@code SecurityFilterChain} that duplicates
     *       the plugin's OAuth2 client filter chain</li>
     * </ul>
     */
    private static final Set<String> EXCLUDED_AUTO_CONFIGURATIONS = [
            'org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration',
            'org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration',
            'org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration',
            'org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration',
            'org.springframework.boot.security.autoconfigure.actuate.web.servlet.ManagementWebSecurityAutoConfiguration',
            'org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration',
            'org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration',
    ].toSet().asImmutable()

    @Override
    boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        autoConfigurationClasses.collect {
            !enabled || !(it in EXCLUDED_AUTO_CONFIGURATIONS)
        } as boolean[]
    }

    /**
     * Returns the set of auto-configuration class names that this filter excludes.
     * Exposed for testing and diagnostic purposes.
     *
     * @return unmodifiable set of excluded class names
     */
    static Set<String> getExcludedAutoConfigurations() {
        EXCLUDED_AUTO_CONFIGURATIONS
    }
}
