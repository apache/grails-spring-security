<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='default.edit.label' entityNameMessageCode='role.label' entityNameDefault='Role'/>
</head>
<body>
<h3><g:message code='default.edit.label' args='[entityName]'/></h3>
<s2ui:form type='update' beanName='role' useToken="true">
	<s2ui:tabs elementId='tabs' height='150' data='${tabData}'>
		<s2ui:tab name='roleinfo' height='150'>
			<table>
				<tbody>
				<s2ui:textFieldRow name='authority' labelCodeDefault='Authority'/>
				</tbody>
			</table>
		</s2ui:tab>
		<s2ui:tab name='users' height='150'>
			<g:if test='${users.empty}'><g:message code='spring.security.ui.role_no_users'/></g:if>
			<g:each var='u' in='${users}'>
			<g:link controller='user' action='edit' id='${u.id}'>${uiPropertiesStrategy.getProperty(u, 'username')}</g:link><br/>
			</g:each>
		</s2ui:tab>
	</s2ui:tabs>
	<div style='float:left; margin-top: 10px;'>
		<s2ui:submitButton/>
		<g:if test='${role}'><s2ui:deleteButton/></g:if>
	</div>
</s2ui:form>
<g:if test='${role}'><s2ui:deleteButtonForm instanceId='${role.id}' useToken="true"/></g:if>
</body>
</html>
