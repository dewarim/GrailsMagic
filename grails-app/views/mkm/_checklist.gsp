<html>
<head><title><g:message code="mtg.checklist"/></title>

<style>
    .odd {
        background-color: seashell;
    }
    th{
        padding-left:1ex;
        padding-right:1ex;
    }
    td{
        padding:4px;
    }
    footer{
        margin-top: 1em;
    }
</style>
</head>
<body>

<table>
    <thead>
    <tr>
        <th>
            <g:message code="name.label"/>            
        </th>
        <th>
            <g:message code="mkm.language"/>
        </th>
        <th>
            <g:message code="mtg.foil.status"/>
        </th>
        <th>
            <g:message code="item.count"/>
        </th>
        <th>
            <g:message code="mtg.check.boxes"/>
        </th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${articles}" var="article" status="i">
        <tr class="${i % 2 ? 'odd':'even'}">
            <td>${article.product.getOriginalName()}</td>
            <td>${article.language.languageName}</td>
            <td>
                <g:if test="${article.foil}">
                    <g:message code="is.true"/>
                </g:if>
            </td>
            <td style="text-align: center;">${article.count}</td>
            <td>                
                <%
                    // TODO: create tag for this
                    (1..article.count).each{
                        out << '[_]'
                    }
                %>
            </td>
            
        </tr>
    </g:each>
    </tbody>
</table>
<footer>
    <g:message code="generated.by"/>
</footer>
</body>
</html> 