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
package specs

import java.net.http.HttpRequest
import java.nio.charset.StandardCharsets

import spock.lang.IgnoreIf
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Specification

import grails.testing.mixin.integration.Integration
import org.apache.grails.testing.http.client.HttpClientSupport

@Integration
@IgnoreIf({ System.getProperty('TESTCONFIG') != 'putWithParams' })
@Issue('https://github.com/apache/grails-spring-security/issues/554')
class TestFormParamsControllerSpec extends Specification implements HttpClientSupport {

    @Shared String USERNAME = 'Admin'
    @Shared String PASSWORD = 'myPassword'

    void 'PUT request with no parameters'() {
        when: 'A PUT request with no parameters is made'
        def response = httpPut(
                '/testFormParams/permitAll',
                '',
                'application/x-www-form-urlencoded'
        )

        then: 'the controller responds with the correct status and parameters are null'
        response.assertEquals(200, 'username: null, password: null')
    }

    void 'PUT request with parameters in the URL'() {
        when: 'A PUT request with parameters is made'
        def response = httpPut(
                urlWithParams('/testFormParams/permitAll', username: USERNAME, password: PASSWORD),
                '',
                'application/x-www-form-urlencoded'
        )

        then: 'the controller responds with the correct status and parameters are extracted'
        response.assertEquals(200, "username: $USERNAME, password: $PASSWORD")
    }

    void 'PUT request with parameters as x-www-form-urlencoded'() {
        when: 'A PUT request with form params is made'
        def request = newHttpRequestWith('/testFormParams/permitAll') {
            method('PUT', formBodyWith(username: USERNAME, password: PASSWORD))
            header('Content-Type', 'application/x-www-form-urlencoded')
        }
        def response = sendHttpRequest(request)

        then: 'the controller responds with the correct status and parameters are extracted'
        response.assertEquals(200, "username: $USERNAME, password: $PASSWORD")
    }

    void 'PUT request with NULL Content-Type and parameters in the URL'() {
        when: 'A PUT request with no parameters is made'
        def url = urlWithParams('/testFormParams/permitAll', username: USERNAME, password: PASSWORD)
        def request = newHttpRequestWith(url) {
            method('PUT', HttpRequest.BodyPublishers.ofString(''))
        }
        def response = sendHttpRequest(request)

        then: 'the controller responds with the correct status and parameters are extracted'
        response.assertEquals(200, "username: $USERNAME, password: $PASSWORD")
    }

    void 'PUT request with NULL Content-Type'() {
        when: 'A PUT request with NULL Content-Type is made'
        def request = newHttpRequestWith('/testFormParams/permitAll') {
            method('PUT', HttpRequest.BodyPublishers.noBody())
        }
        def response = sendHttpRequest(request)

        then: "the controller responds with the correct status and parameters are null"
        response.assertEquals(200, 'username: null, password: null')
    }

    void 'PATCH request with no parameters'() {
        when: 'A PATCH request with no parameters is made'
        def response = httpPatch(
                '/testFormParams/permitAll',
                '',
                'application/x-www-form-urlencoded'
        )

        then: 'the controller responds with the correct status and parameters are null'
        response.assertEquals(200, 'username: null, password: null')
    }

    void 'PATCH request with parameters in the URL'() {
        when:
        def response = httpPatch(
                urlWithParams('/testFormParams/permitAll', username: USERNAME, password: PASSWORD),
                '',
                'application/x-www-form-urlencoded'
        )

        then: 'the controller responds with the correct status and parameters are extracted'
        response.assertEquals(200, "username: $USERNAME, password: $PASSWORD")
    }

    void 'PATCH request with parameters as x-www-form-urlencoded'() {
        when: 'A PATCH request with form params is made'
        def request = newHttpRequestWith('/testFormParams/permitAll') {
            method('PATCH', formBodyWith(username: USERNAME, password: PASSWORD))
            header('Content-Type', 'application/x-www-form-urlencoded')
        }
        def response = sendHttpRequest(request)

        then: 'the controller responds with the correct status and parameters are extracted'
        response.assertEquals(200, "username: $USERNAME, password: $PASSWORD")
    }

    void 'PUT request to secured endpoint with parameters as x-www-form-urlencoded'() {
        when: 'A PUT request with form params is made to a secured endpoint'
        def request = newHttpRequestWith('/testFormParams/permitAdmin') {
            method('PUT', formBodyWith(username: USERNAME, password: PASSWORD))
            header('Content-Type', 'application/x-www-form-urlencoded')
        }
        def response = sendHttpRequest(request)

        then: 'the request is not processed by the controller'
        response.assertNotEquals(200, "username: $USERNAME, password: $PASSWORD") // Client redirects to login page
    }

    void 'PATCH request to secured endpoint with parameters as x-www-form-urlencoded'() {
        when: 'A PATCH request with form params is made to a secured endpoint'
        def request = newHttpRequestWith('/testFormParams/permitAdmin') {
            method('PATCH', formBodyWith(username: USERNAME, password: PASSWORD))
            header('Content-Type', 'application/x-www-form-urlencoded')
        }
        def response = sendHttpRequest(request)

        then: 'the request is not processed by the controller'
        response.assertNotEquals(200, "username: $USERNAME, password: $PASSWORD") // Client redirects to login page
    }

    void 'PATCH request with NULL Content-Type and parameters in the URL'() {
        when:
        def url = urlWithParams('/testFormParams/permitAll', username: USERNAME, password: PASSWORD)
        def request = newHttpRequestWith(url) {
            method('PATCH', HttpRequest.BodyPublishers.noBody())
        }
        def response = sendHttpRequest(request)

        then: 'the controller responds with the correct status and parameters are extracted'
        response.assertEquals(200, "username: $USERNAME, password: $PASSWORD")
    }

    void 'PATCH request with NULL Content-Type'() {
        when: 'A PATCH request with NULL Content-Type is made'
        def request = newHttpRequestWith('/testFormParams/permitAll') {
            method('PATCH', HttpRequest.BodyPublishers.noBody())
        }
        def response = sendHttpRequest(request)

        then: 'the controller responds with the correct status and parameters are null'
        response.assertEquals(200, 'username: null, password: null')
    }

    static String urlWithParams(Map<String, String> params, String url) {
        if (!params) {
            return url
        }
        def queryString = params.collect { key, value ->
            "${encode(key)}=${encode(value)}"
        }.join('&')
        "$url${url.contains('?') ? '&' : '?'}$queryString"
    }

    private static HttpRequest.BodyPublisher formBodyWith(Map<String, String> form) {
        HttpRequest.BodyPublishers.ofString(toFormUrlEncoded(form))
    }

    private static String toFormUrlEncoded(Map<String, String> form) {
        form.collect { key, value ->
            "${encode(key)}=${encode(value)}"
        }.join('&')
    }

    private static String encode(String value) {
        URLEncoder.encode(value, StandardCharsets.UTF_8)
    }
}
