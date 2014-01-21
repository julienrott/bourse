<g:each in="${recos}" var="reco" status="i">
	<tr class="${i%2 == 0 ? 'odd' : ''}" id="${reco.id}">
		<td style="display: none;">
			${reco.content}
		</td>
		<td>
			${formatDate(format: "dd/MM/yyyy HH:mm", date: reco.dateReco)} ${reco.source?.nom}
		</td>
		<td>
			<a href="${reco.url}" target="_blank">${reco.title}</a>
		</td>
	</tr>
</g:each>

<g:if test="${!params.search || params.search.equals("")}">
	<util:remoteNonStopPageScroll action='filter' controller="bourse"  
			total="${total}" update="recos" loadingHTML="loadingDivId"
			heightOffset="500" onComplete="tooltip"/>
</g:if>