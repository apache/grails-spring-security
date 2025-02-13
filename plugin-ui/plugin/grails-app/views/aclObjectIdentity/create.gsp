<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='default.create.label' entityNameMessageCode='aclObjectIdentity.label' entityNameDefault='AclObjectIdentity'/>
</head>
<body>
<div class="body">
	<s2ui:formContainer type='save' beanType='aclObjectIdentity' focus='objectId'>
		<s2ui:form useToken="true">
			<div class="dialog">
				<br/>
				<table>
					<tbody>
					<s2ui:selectRow name='aclClass.id' from='${classes}' labelCodeDefault='AclClass' optionValue='className' noSelection="['null': '']"/>
					<s2ui:textFieldRow name='objectId' size='50' labelCodeDefault='Object ID'/>
					<s2ui:selectRow name='owner.id' from='${sids}' labelCodeDefault='Owner' optionValue='sid' noSelection="['null': '']"/>
					<s2ui:textFieldRow name='parent.id' size='50' labelCodeDefault='Parent'/>
					<s2ui:checkboxRow name='entriesInheriting' labelCodeDefault='Entries Inheriting'/>
					<tr><td>&nbsp;</td></tr>
					<tr class="prop"><td valign="top"><s2ui:submitButton/></td></tr>
					</tbody>
				</table>
			</div>
		</s2ui:form>
	</s2ui:formContainer>
</div>
</body>
</html>
