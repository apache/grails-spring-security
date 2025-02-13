<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='default.edit.label' entityNameMessageCode='aclClass.label' entityNameDefault='AclClass'/>
</head>
<body>
<div class="body">
	<s2ui:formContainer type='update' beanType='aclClass' focus='className' height='300'>
		<s2ui:form useToken="true">
			<div class="dialog">
				<br/>
				<table>
					<tbody>
					<s2ui:textFieldRow name='className' size='60' labelCodeDefault='Class Name'/>
					<tr>
						<td colspan='2'><g:link action='search' controller='aclObjectIdentity' params='["aclClass.id": aclClass.id]'><g:message code='spring.security.ui.aclClass.edit.viewOids'/></g:link></td>
					</tr>
					<tr>
						<td colspan='2'><g:link action='search' controller='aclEntry' params='["aclClass.id": aclClass.id]'><g:message code='spring.security.ui.aclClass.edit.viewEntries'/></g:link></td>
					</tr>
					</tbody>
				</table>
			</div>
			<div style='float:left; margin-top: 10px;'>
				<s2ui:submitButton/>
				<g:if test='${aclClass}'><s2ui:deleteButton/></g:if>
			</div>
		</s2ui:form>
	</s2ui:formContainer>
	<g:if test='${aclClass}'><s2ui:deleteButtonForm instanceId='${aclClass.id}' useToken="true"/></g:if>
</div>
</body>
</html>
