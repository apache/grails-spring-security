<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="${layoutUi}" />
        <s2ui:title messageCode='default.edit.label' entityNameMessageCode='profile.label' entityNameDefault='Profile'/>
    </head>
    <body>
        <div id="edit-profile" class="body" role="main">
            <g:hasErrors bean="${this.profile}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.profile}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
             <s2ui:formContainer type='update' beanType='profile' focus='myQuestion1'>
                <s2ui:form useToken="true">
                    <div class="dialog">
                        <br/>
                        <table>
                            <tbody>
                                
                                    <s2ui:textFieldRow name='myQuestion1' size='50' labelCodeDefault='myQuestion1'/>
                                        <s2ui:textFieldRow name='myAnswer1' size='50' labelCodeDefault='myAnswer1'/>
                                
                                    <s2ui:textFieldRow name='myQuestion2' size='50' labelCodeDefault='myQuestion2'/>
                                        <s2ui:textFieldRow name='myAnswer2' size='50' labelCodeDefault='myAnswer2'/>
                                
                                <s2ui:selectRow name='user.id' from='${users}' labelCodeDefault='user' optionValue='${lookupProp}' />
                            </tbody>
                        </table>
                    </div>
                    <div style='float:left; margin-top: 10px;'>
                        <s2ui:submitButton/>
                            <s2ui:deleteButton/>
                    </div>
                </s2ui:form>
            </s2ui:formContainer>
            <s2ui:deleteButtonForm instanceId='${profile.id}' useToken="true"/>
        </div>
    </body>
</html>
