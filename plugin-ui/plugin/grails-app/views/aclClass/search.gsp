<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='spring.security.ui.aclClass.search'/>
</head>
<body>
<div>
	<s2ui:formContainer type='search' beanType='aclClass' height='300'>
		<s2ui:searchForm colspan='2'>
			<tr>
				<td><g:message code='aclClass.className.label' default='Class Name'/>:</td>
				<td><g:textField name='className' size='60' maxlength='255' autocomplete='off' value='${className}'/></td>
			</tr>
		</s2ui:searchForm>
	</s2ui:formContainer>
	<g:if test='${searched}'>
	<div class="list">
		<table>
			<thead>
			<tr>
				<s2ui:sortableColumn property='className' titleDefault='Class Name'/>
			</tr>
			</thead>
			<tbody>
			<g:each in='${results}' status='i' var='aclClass'>
				<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
					<td><g:link action='edit' id='${aclClass.id}'>${uiPropertiesStrategy.getProperty(aclClass, 'className')}</g:link></td>
				</tr>
			</g:each>
			</tbody>
		</table>
	</div>
	<s2ui:paginate total='${totalCount}'/>
	</g:if>
</div>
<s2ui:ajaxSearch paramName='className'/>
</body>
</html>
