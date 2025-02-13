<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="${layoutUi}" />
        <s2ui:title messageCode='default.list.label' entityNameMessageCode='profile.label' entityNameDefault='Profile'/>
    </head>
    <body>
        <div id="list-profile" class="body" role="main">
           <div class="list">
           		<table>
           			<thead>
           			<tr>
           				<s2ui:sortableColumn property='id' titleDefault='ID'/>
                     
                        <s2ui:sortableColumn property='myQuestion1' titleDefault='myQuestion1'/>
                        <s2ui:sortableColumn property='myAnswer1' titleDefault='myAnswer1'/>
                     
                        <s2ui:sortableColumn property='myQuestion2' titleDefault='myQuestion2'/>
                        <s2ui:sortableColumn property='myAnswer2' titleDefault='myAnswer2'/>
                     
                     <s2ui:sortableColumn property='user' titleDefault='user'/>
           			</tr>
           			</thead>
           			<tbody>
           			<g:each in='${results}' status='i' var='entry'>
           				<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
           					<td><g:link action='edit' id='${entry.id}'>${entry.id}</g:link></td>
           					 
                                                    <td>${entry.myQuestion1}</td>
                                                    <td>${entry.myAnswer1}</td>
                                                 
                                                    <td>${entry.myQuestion2}</td>
                                                    <td>${entry.myAnswer2}</td>
                                                 
           					<td><g:link action='edit' controller='User' id='${entry.userId}'>${entry.user}</g:link></td>
           				</tr>
           			</g:each>
           			</tbody>
           		</table>
           	</div>
           	<s2ui:paginate total='${totalCount}'/>
        </div>
    </body>
</html>