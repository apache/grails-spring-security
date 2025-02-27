<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='default.create.label' entityNameMessageCode='aclSid.label' entityNameDefault='AclSid'/>
</head>
<body>
<div class="body">
	<s2ui:formContainer type='save' beanType='aclSid' focus='sid'>
		<s2ui:form useToken="true">
			<div class="dialog">
				<br/>
				<table>
					<tbody>
					<s2ui:textFieldRow name='sid' size='50' labelCodeDefault='SID'/>
					<s2ui:checkboxRow name='principal' labelCodeDefault='Principal'/>
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
