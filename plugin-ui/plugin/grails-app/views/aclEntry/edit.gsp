<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='default.edit.label' entityNameMessageCode='aclEntry.label' entityNameDefault='AclEntry'/>
</head>
<body>
<div class="body">
	<s2ui:formContainer type='update' beanType='aclEntry' focus='sid'>
		<s2ui:form useToken="true">
			<div class="dialog">
				<br/>
				<table>
					<tbody>
					<s2ui:textFieldRow name='aclObjectIdentity.id' size='50' labelCodeDefault='AclObjectIdentity'/>
					<s2ui:textFieldRow name='aceOrder' size='50' labelCodeDefault='Ace Order'/>
					<s2ui:selectRow name='sid.id' from='${sids}' labelCodeDefault='SID' optionValue='sid'/>
					<s2ui:textFieldRow name='mask' size='50' labelCodeDefault='Mask'/>
					<s2ui:checkboxRow name='granting' labelCodeDefault='Granting'/>
					<s2ui:checkboxRow name='auditSuccess' labelCodeDefault='Audit Success'/>
					<s2ui:checkboxRow name='auditFailure' labelCodeDefault='Audit Failure'/>
					</tbody>
				</table>
			</div>
			<div style='float:left; margin-top: 10px;'>
				<s2ui:submitButton/>
				<g:if test='${aclEntry}'><s2ui:deleteButton/></g:if>
			</div>
		</s2ui:form>
	</s2ui:formContainer>
	<g:if test='${aclEntry}'><s2ui:deleteButtonForm instanceId='${aclEntry.id}' useToken="true"/></g:if>
</div>
</body>
</html>
