<g:if test="${games?.size()}">
    <ul>
        <g:each in="${games}" var="game">
            <li>${game.name}</li>
        </g:each>
    </ul>
</g:if>
<g:remoteLink update="mkm-game-list" controller="mkm" action="updateGames">
    <g:message code="mkm.update.games"/>
</g:remoteLink>