<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='default.create.label' entityNameMessageCode='role.label' entityNameDefault='Role'/>
</head>
<body>
<div class="body">
	<s2ui:formContainer type='save' beanType='role' focus='authority' height='300'>
		<s2ui:form useToken="true">
			<div class="dialog">
				<br/>
				<table>
					<tbody>
					<s2ui:textFieldRow name='authority' size='50' labelCodeDefault='Authority'/>
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
