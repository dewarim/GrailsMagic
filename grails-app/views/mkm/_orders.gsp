<table>
    <thead>
    <tr>
        <th><g:message code="mcm.order.seller"/></th>
        <th><g:message code="mcm.order.buyer"/></th>
        <th><g:message code="mcm.order.articleValue"/></th>
        <th><g:message code="mcm.order.totalValue"/></th>
        <th><g:message code="actions"/> </th>
    </tr>
    </thead>
    <tbody>    
    <g:each in="${orders}" var="order">
        <tr>
        <td>${order.seller?.username}</td>
        <td>${order.buyer?.username}</td>
            <td>
            <g:formatNumber number="${order.articleValue / 100}" currencySymbol="EUR" minFractionDigits="2"/>


            </td>
            <td>
                <g:formatNumber number="${order.articleValue / 100}" currencySymbol="EUR" minFractionDigits="2"/>
            </td>
            <td>
                <g:link target="_blank" action="renderChecklist" controller="mkm" id="${order.id}">
                    <g:message code="link.to.checklist"/>
                </g:link>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>
