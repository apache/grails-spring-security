package com.testacl

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes='username')
@ToString(includes='username')
class User implements Serializable {

	private static final long serialVersionUID = 1

	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this)*.role
	}

	static constraints = {
		username blank: false, unique: true
		password blank: false
	}

	static mapping = {
		table name: '`user`'
	}
}
