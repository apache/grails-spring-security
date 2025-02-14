<html>

<head>
<title>Create Report</title>
</head>

<body>

<div class="body">

<h1>Create Report</h1>

<g:if test="${flash.message}">
<div class="message">${flash.message}</div>
</g:if>

<g:hasErrors bean="${report}">
<div class="errors">
	<g:renderErrors bean="${report}" as="list" />
</div>
</g:hasErrors>

<g:form action='save'>

	<div class="dialog">
		<table>
			<tbody>
			<tr class="prop">
				<td valign="top" class="name">Name</td>
				<td valign="top" class="value"><g:textField name='name' value="${report?.name}" /></td>
			</tr>

			<tr class="prop">
				<td valign="top" class="name">Number</td>
				<td valign="top" class="value"><g:textField name='number' value="${report?.number}" /></td>
			</tr>
			</tbody>
		</table>
	</div>

	<div class="buttons">
		<span class="button"><g:submitButton name='create' class='save' value='Create' /></span>
	</div>

</g:form>

</div>

</body>
</html>
