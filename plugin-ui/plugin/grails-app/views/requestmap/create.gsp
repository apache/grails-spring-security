<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='default.create.label' entityNameMessageCode='requestmap.label' entityNameDefault='Requestmap'/>
</head>
<body>
<div class="body">
	<s2ui:formContainer type='save' beanType='requestmap' focus='url' height='350'>
		<s2ui:form useToken="true">
			<div class="dialog">
				<br/>
				<table>
					<tbody>
					<s2ui:textFieldRow name='url' size='50' labelCodeDefault='URL'/>
					<s2ui:textFieldRow name='configAttribute' size='50' labelCodeDefault='Config Attribute'/>
					<g:if test='${hasHttpMethod}'>
					<s2ui:selectRow name='httpMethod' noSelection="['': '']" labelCodeDefault='HttpMethod'
					                from='${org.springframework.http.HttpMethod.values()}' optionKey='${{it}}'/>
					</g:if>
					<tr><td>&nbsp;</td></tr>
					<tr class="prop"><td valign="top"><s2ui:submitButton/></td></tr>
					</tbody>
				</table>
			</div>
		</s2ui:form>
	</s2ui:formContainer>
</div>
</body>
</html>
