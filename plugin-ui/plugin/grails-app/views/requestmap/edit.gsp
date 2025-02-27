<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='default.edit.label' entityNameMessageCode='requestmap.label' entityNameDefault='Requestmap'/>
</head>
<body>
<div class="body">
	<s2ui:formContainer type='update' beanType='requestmap' focus='url' height='350'>
		<s2ui:form useToken="true">
			<div class="dialog">
				<br/>
				<table>
					<tbody>
					<s2ui:textFieldRow name='url' size='50' labelCodeDefault='URL'/>
					<s2ui:textFieldRow name='configAttribute' size='50' labelCodeDefault='Config Attribute'/>
					<g:if test='${hasHttpMethod}'>
					<s2ui:selectRow name='httpMethod' noSelection="['': '']" optionKey='${{it}}'
					                labelCodeDefault='HttpMethod' from='${org.springframework.http.HttpMethod.values()}'/>
					</g:if>
					</tbody>
				</table>
			</div>
			<div style='float:left; margin-top: 10px;'>
				<s2ui:submitButton/>
				<g:if test='${requestmap}'>
					<s2ui:deleteButton/>
				</g:if>
			</div>
		</s2ui:form>
	</s2ui:formContainer>
	<g:if test='${requestmap}'>
		<s2ui:deleteButtonForm instanceId='${requestmap.id}' useToken="true"/>
	</g:if>
</div>
</body>
</html>
