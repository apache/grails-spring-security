<html>
<head>
	<meta name="layout" content="${layoutRegister}"/>
	<s2ui:title messageCode='spring.security.ui.register.title'/>
</head>
<body>
<s2ui:formContainer type='register' focus='username' width='800px'>
	<s2ui:form beanName='registerCommand'>
		<g:if test='${emailSent}'>
		<br/>
		<g:message code='spring.security.ui.register.sent'/>
		</g:if>
		<g:else>
		<br/>
		<table>
			<tbody>
			<s2ui:textFieldRow name='username' size='40' labelCodeDefault='Username'/>
			<s2ui:textFieldRow name='email' size='40' labelCodeDefault='E-mail'/>
			<s2ui:passwordFieldRow name='password' size='40' labelCodeDefault='Password'/>
			<s2ui:passwordFieldRow name='password2' size='40' labelCodeDefault='Password (again)'/>
			</tbody>
		</table>
		<s2ui:submitButton elementId='submit' messageCode='spring.security.ui.register.submit'/>
		</g:else>
	</s2ui:form>
</s2ui:formContainer>
</body>
</html>
