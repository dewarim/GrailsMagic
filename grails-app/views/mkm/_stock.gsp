<table>
    <thead>
    <tr>
        <th><g:message code="mkm.image"/> </th>
        <th><g:message code="mkm.productId"/></th>
        <th><g:message code="mkm.productName"/></th>
        <th><g:message code="mkm.comment"/> </th>
        <th><g:message code="mkm.amount"/></th>
        <th><g:message code="mkm.price"/> </th>
        
    </tr>
    </thead>
    <tbody>
    <g:each in="${cards}" var="card">
        <tr>
            <th><img src="${createLink([action: 'show', controller: 'image', id:card.product.image?.id])}" alt="${card.product.originalName}"/> </th>
            <td>${card.product.productId}</td>            
            <td>${card.product.originalName}</td>
            <td>${card.count}</td>
            <td>
                <g:formatNumber number="${card.price / 100}" currencySymbol="EUR" minFractionDigits="2"/>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>
