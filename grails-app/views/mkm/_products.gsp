<g:if test="${products?.size() > 0}">
    
<table>
    <thead>
    <tr>
        <th><g:message code="mkm.image"/> </th>
        <th><g:message code="mkm.productName"/> </th>
        <th><g:message code="mkm.product.sell"/> </th>
        <th><g:message code="mkm.product.low"/> </th>
        <th><g:message code="mkm.product.average"/> </th>
    </tr>
    </thead>
<g:each in="${products}" var="product">
    <tr>
        <td>
            <img src="${createLink(controller: 'image', action: 'show', id:product.image?.id)}" alt="${product.originalName}">
        </td>
        <td>
            ${product.originalName}
        </td>
        <g:if test="${product.priceGuides?.size() > 0}">
        <td>
            ${product.priceGuides?.first()?.sell}
        </td>
        <td>
            ${product.priceGuides?.first()?.low}
        </td>
        <td>
            ${product.priceGuides?.first()?.average}
        </td>
        </g:if>
        <g:else>
            <td>---</td>
            <td>---</td>
            <td>---</td>
        </g:else>
    </tr>    
</g:each>
</table>
</g:if>
<g:else>
<g:message code="mkm.found.nothing"/>    
</g:else>
