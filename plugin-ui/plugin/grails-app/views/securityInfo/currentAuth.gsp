<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<title><g:message code='spring.security.ui.menu.securityInfo.currentAuth'/></title>
</head>
<body>
<s2ui:securityInfoTable type='currentAuth' headerCodes='name,value'>
	<tr class='even'>
		<td><g:message code='spring.security.ui.info.currentAuth.label.authorities'/></td>
		<td>${auth.authorities}</td>
	</tr>
	<tr class='odd'>
		<td><g:message code='spring.security.ui.info.currentAuth.label.details'/></td>
		<td>${auth.details}</td>
	</tr>
	<tr class='even'>
		<td><g:message code='spring.security.ui.info.currentAuth.label.principal'/></td>
		<td>${auth.principal}</td>
	</tr>
	<tr class='odd'>
		<td><g:message code='spring.security.ui.info.currentAuth.label.name'/></td>
		<td>${auth.name}</td>
	</tr>
</s2ui:securityInfoTable>
</body>
</html>
