<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<title><g:message code='spring.security.ui.menu.securityInfo.providers'/></title>
</head>
<body>
<s2ui:securityInfoTable type='providers' items='${providers}' headerCodes='className'>
	<td>${it.getClass().name}</td>
</s2ui:securityInfoTable>
</body>
</html>
