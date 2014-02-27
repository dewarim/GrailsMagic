<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
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

</body>
</html>