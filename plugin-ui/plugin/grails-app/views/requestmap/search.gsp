<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='spring.security.ui.requestmap.search'/>
</head>
<body>
<div class="body">
	<s2ui:formContainer type='search' beanType='requestmap' height='350'>
		<s2ui:searchForm colspan='2'>
			<tr>
				<td><g:message code='requestmap.url.label' default='URL'/>:</td>
				<td><g:textField name='url' size='50' maxlength='255' autocomplete='off' value='${url}'/></td>
			</tr>
			<tr>
				<td><g:message code='requestmap.configAttribute.label' default='Config Attribute'/>:</td>
				<td><g:textField name='configAttribute' size='50' maxlength='255' autocomplete='off' value='${configAttribute}'/></td>
			</tr>
			<g:if test='${hasHttpMethod}'>
			<tr>
				<td><g:message code='requestmap.httpMethod.label' default='HttpMethod'/>:</td>
				<td>
					<g:select name='httpMethod' from='${org.springframework.http.HttpMethod.values()}' value='${httpMethod}' noSelection="['null': 'All']"/>
				</td>
			</tr>
			</g:if>
		</s2ui:searchForm>
	</s2ui:formContainer>
	<g:if test='${searched}'>
	<div class="list">
		<table>
			<thead>
			<tr>
				<s2ui:sortableColumn property='url' titleDefault='URL'/>
				<s2ui:sortableColumn property='configAttribute' titleDefault='Config Attribute'/>
				<g:if test='${hasHttpMethod}'><s2ui:sortableColumn property='httpMethod' titleDefault='HttpMethod'/></g:if>
			</tr>
			</thead>
			<tbody>
			<g:each in='${results}' status='i' var='requestmap'>
				<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
					<td><g:link action='edit' id='${requestmap.id}'>${uiPropertiesStrategy.getProperty(requestmap, 'url')}</g:link></td>
					<td>${uiPropertiesStrategy.getProperty(requestmap, 'configAttribute')}</td>
					<g:if test='${hasHttpMethod}'><td>${uiPropertiesStrategy.getProperty(requestmap, 'httpMethod')}</td></g:if>
				</tr>
			</g:each>
			</tbody>
		</table>
	</div>
	<s2ui:paginate total='${totalCount}'/>
	</g:if>
</div>
<s2ui:ajaxSearch paramName='url'/>
<s2ui:ajaxSearch paramName='configAttribute' focus='false'/>
</body>
</html>
