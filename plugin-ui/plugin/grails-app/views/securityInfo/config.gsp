<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<title><g:message code='spring.security.ui.menu.securityInfo.config'/></title>
	<s2ui:stylesheet src='jquery.dataTables.css'/>
</head>
<body>
<div id="configHolder">
	<table id="config" cellpadding="0" cellspacing="0" border="0" class="display">
		<caption><g:message code='spring.security.ui.menu.securityInfo.config'/></caption>
		<thead>
		<tr>
			<th><g:message code='spring.security.ui.info.config.header.name'/></th>
			<th><g:message code='spring.security.ui.info.config.header.value'/></th>
		</tr>
		</thead>
		<tbody>
		<g:each var='entry' in='${conf}'>
<%
def key = entry.key
if (key.startsWith('failureHandler.exceptionMappings.')) {
	key = key - 'failureHandler.exceptionMappings.'
	key = 'failureHandler.exceptionMappings. ' + key.replaceAll('\\.', '\\. ')
}
def value = entry.value
if (value instanceof Class) {
	value = value.name.replaceAll('\\.', '\\. ')
}
%>
			<tr>
				<td>${key}</td>
				<td>${value}</td>
			</tr>
		</g:each>
		</tbody>
	</table>
</div>
<s2ui:deferredScript src='jquery/jquery.dataTables.js'/>
<s2ui:documentReady>
$('#config').DataTable();
</s2ui:documentReady>
</body>
</html>
