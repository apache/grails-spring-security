<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<title><g:message code='spring.security.ui.menu.securityInfo.secureChannel'/></title>
</head>
<body>
<g:if test='${requestMap}'>
<s2ui:securityInfoTable type='secureChannel' items='${requestMap}' headerCodes='pattern,attributes'>
	<td>${it.key}</td>
	<td>${it.value}</td>
</s2ui:securityInfoTable>
</g:if>
<g:else>
<h3><g:message code='spring.security.ui.info.secureChannel.disabled'/></h3>
</g:else>
</body>
</html>
