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

<h2><g:message code="mkm.article.search"/> </h2>
<g:formRemote name="mkm-article-search" url="[controller:'mkm', action:'doSearchArticles']" update="mkm-product-list">
    <label for="gameId3">
        <g:message code="mkm.game"/>
    </label>
    <g:select id="gameId3" name="gameId" from="${Game.list()}" optionKey="gameId" optionValue="${{it.name}}"/>
    <label for="exactMatch3">
        <g:message code="mkm.exact.search"/>
    </label>
    <g:checkBox id="exactMatch3" name="exactMatch"/>

    <label for="languageId3">
        <g:message code="mkm.language"/>
    </label>
    <g:select id="languageId3" name="languageId" from="${Language.list()}" optionKey="idLanguage" optionValue="${{it.languageName}}"/>
    <br>

    <g:textField id="query3" name="query" placeholder="${message(code: 'mkm.query')}"/>
    <g:submitButton name="doSearch3" value="${message(code: 'doSearch')}" />
</g:formRemote>

<hr>
<h3><g:message code="mkm.search.meta.product"/> </h3>
<g:formRemote name="mkm-meta-product-search" url="[controller:'mkm', action:'doSearchMeta']" update="mkm-product-list">
    <label for="gameId2">
        <g:message code="mkm.game"/>
    </label>


    <g:select id="gameId2" name="gameId" from="${Game.list()}" optionKey="gameId" optionValue="${{it.name}}"/>
  
    <label for="languageId2">
        <g:message code="mkm.language"/>
    </label>
    <g:select id="languageId2" name="languageId" from="${Language.list()}" optionKey="idLanguage" optionValue="${{it.languageName}}"/>
    <br>



    <g:textField id="query2" name="query" placeholder="${message(code: 'mkm.query')}"/>
    <g:submitButton id="doSearchMetaProduct" name="doSearch" value="${message(code: 'doSearch')}" />
</g:formRemote>
<hr>

<div id="mkm-product-list">

</div>

<h2><g:message code="mkm.supported.games"/></h2>
<div id="mkm-game-list">
    <g:render template="games" model="[games:games]"/>
</div>
</body>
</html>