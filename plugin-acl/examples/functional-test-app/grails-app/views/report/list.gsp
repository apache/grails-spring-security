<html>

<head>
<title>Report List</title>
</head>

<body>

<div class="body">

<h1>Report List</h1>

<g:if test="${flash.message}">
<div class="message">${flash.message}</div>
</g:if>

<div class="list">
	<table>
		<thead>
		<tr>
			<th>ID</th>
			<th>Name</th>
			<th>Number</th>
		</tr>
		</thead>
		<tbody>
		<g:each in="${reports}" status='i' var='report'>
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>${report.id}</td>
				<td>${report.name}</td>
				<td>${report.number}</td>
			</tr>
		</g:each>
		</tbody>
	</table>
</div>

<div class="paginateButtons">
<g:paginate total="${reportCount}" />
</div>

</div>
</body>
</html>
