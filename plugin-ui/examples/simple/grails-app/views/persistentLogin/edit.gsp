<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='default.edit.label' entityNameMessageCode='persistentLogin.label' entityNameDefault='PersistentLogin'/>
</head>
<body>
<div class="body">
	<s2ui:formContainer type='update' beanType='persistentLogin' focus='token' height='350'>
		<s2ui:form idName='series' useToken="true">
			<div class="dialog">
				<br/>
				<table>
					<tbody>
					<tr class="prop">
						<td valign="top" class="name">
							<label for="series">${message(code: 'persistentLogin.series.label', default: 'Series')}</label>
						</td>
						<td valign="top" class="value">${persistentLogin.series}</td>
					</tr>
					<tr class="prop">
						<td valign="top" class="name">
							<label for="username">${message(code: 'persistentLogin.username.label', default: 'Username')}</label>
						</td>
						<td valign="top" class="value"><g:link action='edit' controller='user' params='[username: persistentLogin.username]'>${persistentLogin.username}</g:link></td>
					</tr>
					<s2ui:textFieldRow name='token' size='50' labelCodeDefault='Token'/>
					<s2ui:dateFieldRow name='lastUsed' size='50' labelCodeDefault='Last Used'/>
					</tbody>
				</table>
			</div>
			<div style='float:left; margin-top: 10px;'>
				<s2ui:submitButton/>
				<g:if test='${persistentLogin}'><s2ui:deleteButton/></g:if>
			</div>
		</s2ui:form>
	</s2ui:formContainer>
	<g:if test='${persistentLogin}'><s2ui:deleteButtonForm instanceId='${persistentLogin.series}' useToken="true"/></g:if>
</div>
</body>
</html>
