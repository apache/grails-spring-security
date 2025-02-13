<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='default.create.label' entityNameMessageCode='aclEntry.label' entityNameDefault='AclEntry'/>
</head>
<body>
<div class="body">
	<s2ui:formContainer type='save' beanType='aclEntry' focus='aclObjectIdentity'>
		<s2ui:form useToken="true">
			<div class="dialog">
				<br/>
				<table>
					<tbody>
					<s2ui:textFieldRow name='aclObjectIdentity.id' size='50' labelCodeDefault='AclObjectIdentity'/>
					<s2ui:textFieldRow name='aceOrder' size='50' labelCodeDefault='Ace Order'/>
					<s2ui:selectRow name='sid.id' from='${sids}' labelCodeDefault='SID' optionValue='sid' noSelection="['null': '']"/>
					<s2ui:textFieldRow name='mask' size='50' labelCodeDefault='Mask'/>
					<s2ui:checkboxRow name='granting' labelCodeDefault='Granting'/>
					<s2ui:checkboxRow name='auditSuccess' labelCodeDefault='Audit Success'/>
					<s2ui:checkboxRow name='auditFailure' labelCodeDefault='Audit Failure'/>
					<tr class="prop"><td valign="top"><s2ui:submitButton/></td></tr>
					</tbody>
				</table>
			</div>
		</s2ui:form>
	</s2ui:formContainer>
</div>
</body>
</html>
