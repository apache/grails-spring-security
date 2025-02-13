<html>

<head>
<title>Show Report</title>
</head>

<body>

<div class="body">

<h1>Show Report</h1>

<g:if test="${flash.message}">
<div class="message">${flash.message}</div>
</g:if>

<div class="dialog">
	<table>
		<tbody>

		<tr class="prop">
			<td valign="top" class="name">ID</td>
			<td valign="top" class="value" id="id">${report.id}</td>
		</tr>

		<tr class="prop">
			<td valign="top" class="name">Name</td>
			<td valign="top" class="value" id="name">${report.name}</td>
		</tr>

		<tr class="prop">
			<td valign="top" class="name">Number</td>
			<td valign="top" class="value" id="number">${report.number}</td>
		</tr>

		</tbody>
	</table>
</div>

</div>
</body>
</html>
