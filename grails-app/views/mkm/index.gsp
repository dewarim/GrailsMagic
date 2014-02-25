<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:message code="mkm.your.articles"/> </title>
</head>

<body>

<h1><g:message code="mkm.head"/></h1>

<p><g:message code="mkm.intro"/></p>
<g:render template="/mkm/stock" model="[cards: cards]"/>

</body>
</html>