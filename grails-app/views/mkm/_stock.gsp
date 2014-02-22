<!-- for now, just output the APIs response here. -->
<table>
    <thead>
    <tr>
        <!-- TODO: add to message.properties -->
        <th><g:message code="mkm.product"/></th>
        <th><g:message code="mkm.productName"/></th>
        <th><g:message code="mkm.amount"/></th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${cards}" var="card">
        <tr>
            <td>${card.product.productId}</td>            
            <td>${card.product.names.first()?.name}</td>
            <td>${card.count}</td>
        </tr>
    </g:each>
    </tbody>
</table>
<!-- TODO: fix: first().name may return any language it seems. -->
<!-- end:cards -->    