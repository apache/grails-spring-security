package grails.plugin.springsecurity.acl

class AclClass {

	String className

	@Override
	String toString() {
		"AclClass id $id, className $className"
	}

	static mapping = {
		className column: 'class'
		version false
	}

	static constraints = {
		className unique: true, blank: false
	}
}
