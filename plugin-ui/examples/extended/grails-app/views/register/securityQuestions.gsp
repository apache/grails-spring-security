<html>
<head>
    <meta name="layout" content="${layoutRegister}"/>
    <s2ui:title messageCode='spring.security.ui.securityQuestions.title'/>
</head>
<body>
<s2ui:formContainer type='securityQuestions' width='50%' >
    <s2ui:form beanName='securityQuestionsCommand' useToken="true">
        <input type="hidden" value="${securityQuestionsCommand?.username}" name="username" id="username" />
            <br/>
            <h3><g:message code='spring.security.ui.securityQuestions.description'/></h3>
            <s2ui:cmdValidationFields domainClassName="${forgotPasswordExtraValidationDomainClassName}" validations="${securityQuestionsCommand.validations}" myfields="${forgotPasswordExtraValidation}"  user="${user}" validationUserLookUpProperty="${validationUserLookUpProperty}" />
            <s2ui:submitButton elementId='submit' messageCode='spring.security.ui.securityQuestions.submit'/>
    </s2ui:form>
</s2ui:formContainer>
</body>
</html>