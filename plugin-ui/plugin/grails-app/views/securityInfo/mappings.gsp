<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<title><g:message code='spring.security.ui.menu.securityInfo.mappings'/></title>
</head>
<body>
<p/>
<g:message code='spring.security.ui.info.mappings.type' args='[securityConfigType]'/>
<s2ui:securityInfoTable type='mappings' items='${configAttributes}' headerCodes='pattern,attribute,method'>
	<g:set var='closure' value='${it.configAttributes.any { ca -> ca.getClass().name.contains("ClosureConfigAttribute") }}'/>
	<td>${it.pattern}</td>
	<td>${closure ? '&lt;closure&gt;' : it.configAttributes.toString()[1..-2]}</td>
	<td>${it.httpMethod ?: 'all'}</td>
</s2ui:securityInfoTable>
</body>
</html>
