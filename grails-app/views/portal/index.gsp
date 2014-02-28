<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="title.grailsMagic"/></title>
</head>

<body>

<h1><g:message code="portal.head"/></h1>

<p><g:message code="portal.intro"/></p>
<ul>
    <li><g:link controller="mkm" action="index"><g:message code="link.to.mkm.index"/></g:link><br>
        <g:message code="link.to.mkm.label"/>
    </li>
    <li>
        <g:link controller="portal" action="links">
            <g:message code="link.to.links"/>
        </g:link>
    </li>
</ul>

<footer>
    <g:message code="wizards.disclaimer"/>
    <br>
    <g:link url="https://github.com/dewarim/GrailsMagic/blob/master/LICENSE"><g:message
            code="link.to.license"/></g:link>
</footer>
</body>
</html>