<g:if test="${products?.size() > 0}">
    
<table>
    <thead>
    <tr>
        <th><g:message code="mkm.image"/> </th>
        <th><g:message code="mkm.productName"/> </th>
    </tr>
    </thead>
<g:each in="${products}" var="product">
    <tr>
        <td>
            <img src="${createLink(controller: 'image', action: 'show', id:product.fetchProducts()?.first()?.id)}" alt="${product.originalName}">
        </td>
        <td>
            ${product.originalName}
        </td>
    </tr>    
</g:each>
</table>
</g:if>
<g:else>
<g:message code="mkm.found.nothing"/>    
</g:else>
