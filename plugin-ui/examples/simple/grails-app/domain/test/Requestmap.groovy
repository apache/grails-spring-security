package test

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import org.springframework.http.HttpMethod

@EqualsAndHashCode(includes=['configAttribute', 'httpMethod', 'url'])
@ToString(includes=['configAttribute', 'httpMethod', 'url'], cache=true, includeNames=true, includePackage=false)
class Requestmap implements Serializable {

	private static final long serialVersionUID = 1

	String configAttribute
	HttpMethod httpMethod
	String url

	Requestmap(String url, String configAttribute, HttpMethod httpMethod = null) {
		this()
		this.configAttribute = configAttribute
		this.httpMethod = httpMethod
		this.url = url
	}

	static constraints = {
		httpMethod nullable: true
		url validator: { String url, Requestmap requestmap, errors ->
			if (!url) return

			Requestmap.withSession {
				def existing = requestmap.httpMethod ?
					Requestmap.findByUrlAndHttpMethod(requestmap.url, requestmap.httpMethod) :
					Requestmap.findByUrlAndHttpMethodIsNull(requestmap.url)
				if (existing) {
					errors.rejectValue('url', 'default.not.unique.message',
						['url', this, url] as Object[],
						"${this.name} invalid; url '$url' must be unique for httpMethod: $requestmap.httpMethod")
				}
			}
		}
	}

	static mapping = {
		cache true
	}
}
