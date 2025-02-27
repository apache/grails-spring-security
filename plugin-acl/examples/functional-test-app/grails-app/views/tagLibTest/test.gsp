<%@ page import="org.springframework.security.acls.domain.BasePermission" %>
<html>
<body>

<g:each in='${reportIdsAndNumbers}' var='entry'>

<%-- single String --%>
	<sec:permitted className='com.testacl.Report' id='${entry.id}' permission='read'>
		test 1 true ${entry.number}<br/>
	</sec:permitted>
	<sec:notPermitted className='com.testacl.Report' id='${entry.id}' permission='read'>
		test 1 false ${entry.number}<br/>
	</sec:notPermitted>

<%-- multiple String --%>
	<sec:permitted className='com.testacl.Report' id='${entry.id}' permission='write,read'>
		test 2 true ${entry.number}<br/>
	</sec:permitted>
	<sec:notPermitted className='com.testacl.Report' id='${entry.id}' permission='read,write'>
		test 2 false ${entry.number}<br/>
	</sec:notPermitted>

<%-- single Permission --%>
	<sec:permitted className='com.testacl.Report' id='${entry.id}' permission='${BasePermission.READ}'>
		test 3 true ${entry.number}<br/>
	</sec:permitted>
	<sec:notPermitted className='com.testacl.Report' id='${entry.id}' permission='${BasePermission.READ}'>
		test 3 false ${entry.number}<br/>
	</sec:notPermitted>

<%-- List of Permission --%>
	<sec:permitted className='com.testacl.Report' id='${entry.id}' permission='${[BasePermission.WRITE,BasePermission.READ]}'>
		test 4 true ${entry.number}<br/>
	</sec:permitted>
	<sec:notPermitted className='com.testacl.Report' id='${entry.id}' permission='${[BasePermission.WRITE,BasePermission.READ]}'>
		test 4 false ${entry.number}<br/>
	</sec:notPermitted>

<%-- single mask int --%>
	<sec:permitted className='com.testacl.Report' id='${entry.id}' permission='${1}'>
		test 5 true ${entry.number}<br/>
	</sec:permitted>
	<sec:notPermitted className='com.testacl.Report' id='${entry.id}' permission='${1}'>
		test 5 false ${entry.number}<br/>
	</sec:notPermitted>

<%-- multiple mask int --%>
	<sec:permitted className='com.testacl.Report' id='${entry.id}' permission='2,1'>
		test 6 true ${entry.number}<br/>
	</sec:permitted>
	<sec:notPermitted className='com.testacl.Report' id='${entry.id}' permission='2,1'>
		test 6 false ${entry.number}<br/>
	</sec:notPermitted>

	<br/>

</g:each>

</body>
</html>
