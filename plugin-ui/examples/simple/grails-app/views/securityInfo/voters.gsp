<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<title><g:message code='spring.security.ui.menu.securityInfo.voters'/></title>
</head>
<body>
<s2ui:securityInfoTable type='voters' items='${voters}' headerCodes='className'>
	<td>${it.getClass().name}</td>
</s2ui:securityInfoTable>
</body>
</html>
