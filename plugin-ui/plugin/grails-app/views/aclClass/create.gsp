<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<s2ui:title messageCode='default.create.label' entityNameMessageCode='aclClass.label' entityNameDefault='AclClass'/>
</head>
<body>
<div class="body">
	<s2ui:formContainer type='save' beanType='aclClass' focus='className' height='300'>
		<s2ui:form useToken="true">
			<div class="dialog">
				<br/>
				<table>
					<tbody>
					<s2ui:textFieldRow name='className' size='60' labelCodeDefault='Class Name'/>
					<tr><td>&nbsp;</td></tr>
					<tr class="prop"><td valign="top"><s2ui:submitButton/></td></tr>
					</tbody>
				</table>
			</div>
		</s2ui:form>
	</s2ui:formContainer>
</div>
<s2ui:documentReady>
	$("#resizable").resizable({
		minHeight: 150,
		minWidth: 200
	});
</s2ui:documentReady>
</body>
</html>
