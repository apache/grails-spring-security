package grails.plugin.springsecurity.acl

class AclSid {

	String sid
	boolean principal

	@Override
	String toString() {
		"AclSid id $id, sid $sid, principal $principal"
	}

	static mapping = {
		version false
	}

	static constraints = {
		principal unique: 'sid'
		sid blank: false, size: 1..255
	}
}
