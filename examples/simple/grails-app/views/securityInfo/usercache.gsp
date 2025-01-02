<html>
<head>
	<meta name="layout" content="${layoutUi}"/>
	<title><g:message code='spring.security.ui.menu.securityInfo.usercache'/></title>
</head>
<body>
<g:if test='${cache}'>
<p/>
<g:message code='spring.security.ui.info.usercache.classname' args='[cache.getClass().name]'/>
<s2ui:securityInfoTable type='usercache' headerCodes='attribute,value'>
	<tr class='even'>
		<td><g:message code='spring.security.ui.info.usercache.label.name'/></td>
		<td>${cache.name}</td>
	</tr>
	<tr class='even'>
		<td colspan='2'>
		<s2ui:securityInfoTable type='usercache.statistics' headerCodes='attribute,value'>
		<tr>
			<td><g:message code='spring.security.ui.info.usercache.label.stats.cacheHits'/></td>
			<td>${cache.statisticsMBean.cacheHits}</td>
		</tr>
		<tr>
			<td><g:message code='spring.security.ui.info.usercache.label.stats.cacheHitPercentage'/></td>
			<td>${cache.statisticsMBean.cacheHitPercentage}</td>
		</tr>
		<tr>
			<td><g:message code='spring.security.ui.info.usercache.label.stats.cacheMisses'/></td>
			<td>${cache.statisticsMBean.cacheMisses}</td>
		</tr>
		<tr>
			<td><g:message code='spring.security.ui.info.usercache.label.stats.cacheMissPercentage'/></td>
			<td>${cache.statisticsMBean.cacheMissPercentage}</td>
		</tr>
		<tr>
			<td><g:message code='spring.security.ui.info.usercache.label.stats.cacheGets'/></td>
			<td>${cache.statisticsMBean.cacheGets}</td>
		</tr>
		<tr>
			<td><g:message code='spring.security.ui.info.usercache.label.stats.cachePuts'/></td>
			<td>${cache.statisticsMBean.cachePuts}</td>
		</tr>
		<tr>
			<td><g:message code='spring.security.ui.info.usercache.label.stats.cacheRemovals'/></td>
			<td>${cache.statisticsMBean.cacheRemovals}</td>
		</tr>
		<tr>
			<td><g:message code='spring.security.ui.info.usercache.label.stats.evictionCount'/></td>
			<td>${cache.statisticsMBean.cacheEvictions}</td>
		</tr>
		</s2ui:securityInfoTable>
	</td>
</tr>
<tr>
	<td colspan='2'>
	<s2ui:securityInfoTable type='usercache.cachedUsers' items='${cache.iterator()}' headerCodes='username,user'>
		<g:if test="${it}">
			<td>${it.key}</td>
			<td>${it.value}</td>
		</g:if>
	</s2ui:securityInfoTable>
	</td>
</tr>
</s2ui:securityInfoTable>
</g:if>
<g:else>
<h3><g:message code='spring.security.ui.info.usercache.disabled'/></h3>
</g:else>
</body>
</html>
