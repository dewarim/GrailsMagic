<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:message code="mkm.your.orders"/> </title>
</head>

<body>

<h1><g:message code="mkm.orders"/></h1>

<p><g:message code="mkm.orders.intro"/></p>

<div id="order-list">
    <g:render template="/mkm/orders" model="[orders: orders]"/>    
</div>

</body>
</html>