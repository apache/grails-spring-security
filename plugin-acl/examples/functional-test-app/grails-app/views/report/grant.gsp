<html>

<head>
<title>Grant Permission</title>
</head>

<body>

<div class="body">

<h1>Grant permission for ${report.name}</h1>

<g:if test="${flash.message}">
<div class="message">${flash.message}</div>
</g:if>

<g:form action='grant'>
	<g:hiddenField name='number' value="${report.number}" />
	<div class="dialog">
	<table>
	<tbody>
		<tr class="prop">
			<td valign="top" class="name">Recipient</td>
			<td valign="top" class="value"><g:textField name='recipient' /></td>
		</tr>
		<tr class="prop">
			<td valign="top" class="name">Permission</td>
			<td valign="top" class="value"><g:textField name='permission' /></td>
		</tr>
	</tbody>
	</table>
	</div>

	<div class="buttons">
	<span class="button"><g:submitButton name='create' class='save' value='Grant' /></span>
	</div>

</g:form>

</div>
</body>
</html>
