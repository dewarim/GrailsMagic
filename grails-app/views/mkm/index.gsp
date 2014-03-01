<%@ page import="de.dewarim.grailsmagic.mkm.Language; de.dewarim.grailsmagic.mkm.Game" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="mkm.your.articles"/></title>
</head>

<body>

<h1><g:message code="mkm.head"/></h1>

<ul>
    <li>
        <g:link controller="mkm" action="updateStock">
            <g:message code="link.to.mkm.updateStock"/>
        </g:link>
    </li>
    <li>
        <g:link controller="mkm" action="showStock">
            <g:message code="link.to.mkm.showStock"/>
        </g:link>
    </li>
</ul>

<h2><g:message code="mkm.search"/> </h2>
<g:formRemote name="mkm-product-search" url="[controller:'mkm', action:'doSearch']" update="mkm-product-list">
    <label for="gameId">
        <g:message code="mkm.game"/>
    </label>
  
    
    <g:select name="gameId" from="${Game.list()}" optionKey="gameId" optionValue="${{it.name}}"/>
    <label for="exactMatch">
        <g:message code="mkm.exact.search"/>
    </label>
    <g:checkBox name="exactMatch"/>
    
    <label for="languageId">
        <g:message code="mkm.language"/>
    </label>
    <g:select name="languageId" from="${Language.list()}" optionKey="idLanguage" optionValue="${{it.languageName}}"/>
    <br>
    
    
    
    <g:textField name="query" placeholder="${message(code: 'mkm.query')}"/>    
    <g:submitButton name="doSearch" value="${message(code: 'doSearch')}" />
</g:formRemote>

<div id="mkm-product-list">

</div>

<h2><g:message code="mkm.supported.games"/></h2>
<div id="mkm-game-list">
    <g:render template="games" model="[games:games]"/>
</div>
</body>
</html>