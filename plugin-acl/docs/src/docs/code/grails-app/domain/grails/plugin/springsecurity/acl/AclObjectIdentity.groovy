package grails.plugin.springsecurity.acl

class AclObjectIdentity extends AbstractAclObjectIdentity {

	Long objectId

	@Override
	String toString() {
		"AclObjectIdentity id $id, aclClass $aclClass.className, " +
		"objectId $objectId, entriesInheriting $entriesInheriting"
	}

	static mapping = {
		version false
		aclClass column: 'object_id_class'
		owner column: 'owner_sid'
		parent column: 'parent_object'
		objectId column: 'object_id_identity'
	}

	static constraints = {
		objectId unique: 'aclClass'
	}
}
