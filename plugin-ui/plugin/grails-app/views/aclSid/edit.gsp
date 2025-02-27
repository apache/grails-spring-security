<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='default.edit.label' entityNameMessageCode='aclSid.label' entityNameDefault='AclSid'/>
</head>
<body>
<div class="body">
	<s2ui:formContainer type='update' beanType='aclSid' focus='sid'>
		<s2ui:form useToken="true">
			<div class="dialog">
				<br/>
				<table>
					<tbody>
					<s2ui:textFieldRow name='sid' size='50' labelCodeDefault='SID'/>
					<s2ui:checkboxRow name='principal' labelCodeDefault='Principal'/>
					<tr>
						<td colspan='2'>
							<g:link action='search' controller='aclObjectIdentity' params='["owner.id": aclSid.id]'><g:message code='spring.security.ui.aclClass.edit.viewOids'/></g:link>
						</td>
					</tr>
					<tr>
						<td colspan='2'>
							<g:link action='search' controller='aclEntry' params='["sid.id": aclSid.id]'><g:message code='spring.security.ui.aclClass.edit.viewEntries'/></g:link>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
			<div style='float:left; margin-top: 10px;'>
				<s2ui:submitButton/>
				<g:if test='${aclSid}'><s2ui:deleteButton/></g:if>
			</div>
		</s2ui:form>
	</s2ui:formContainer>
	<g:if test='${aclSid}'><s2ui:deleteButtonForm instanceId='${aclSid.id}' useToken="true"/></g:if>
</div>
</body>
</html>
