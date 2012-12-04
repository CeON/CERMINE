<%@page pageEncoding="UTF-8"%>
<%@ taglib  prefix="c"      uri="http://java.sun.com/jsp/jstl/core"%>


<script type="text/javascript">
     SyntaxHighlighter.all();
</script>


<script>
    $(function() {
        $("#tabs").tabs();
    });
</script>

<h2>Extraction result</h2>

<div id="tabs">
    <ul>
        <li><a href="#tabs-html">HTML</a></li>
        <li><a href="#tabs-nlm">NLM</a></li>
    </ul>
    <div id="tabs-html">
        <p>A shored result formatted in HTML form. Please get NLM for full extraction result.</p>
        <table class="summaryTable">
            <tr><th>Article&nbsp;title:</th><td>${meta.title}</td></tr>
            <c:forEach var="author" items="${meta.authors}">
                <tr><th>Author:</th><td>${author.name}
                <c:forEach var="aff" items="${author.affiliations}"><br />${aff}</c:forEach>
                <c:forEach var="email" items="${author.emails}"><br />${email}</c:forEach>    
                </td></tr>
            </c:forEach>
            <c:forEach var="editor" items="${meta.editors}">
                <tr><th>Editor:</th><td>${editor.name}
                <c:forEach var="aff" items="${editor.affiliations}"><br />${aff}</c:forEach>
                <c:forEach var="email" items="${editor.emails}"><br />${email}</c:forEach>    
                </td></tr>
            </c:forEach>
            <tr><th>Publisher:</th><td>${meta.publisher}</td></tr>
            <tr><th>Journal&nbsp;title:</th><td>${meta.journalTitle}</td></tr>
            <tr><th>Journal&nbsp;ISSN:</th><td>${meta.journalISSN}</td></tr>
            <tr><th>Volume:</th><td>${meta.volume}</td></tr>
            <tr><th>Issue:</th><td>${meta.issue}</td></tr>
            <tr><th>Pages:</th><td>${meta.pages}</td></tr>
            <tr><th>Abstract:</th><td>${meta.abstractText}</td></tr>
            <tr><th>Keywords:</th><td>${meta.keywordsString}</td></tr>
            <tr><th>DOI:</th><td>${meta.doi}</td></tr>
            <tr><th>URN:</th><td>${meta.urn}</td></tr>
            <tr><th>Publication&nbsp;date:</th><td>${meta.pubDate}</td></tr>
            <tr><th>Received&nbsp;date:</th><td>${meta.receivedDate}</td></tr>
            <tr><th>Revised&nbsp;date:</th><td>${meta.revisedDate}</td></tr>
            <tr><th>Accepted&nbsp;date:</th><td>${meta.acceptedDate}</td></tr>
        </table>
    </div>
    <div id="tabs-nlm">
        <p>Result as an NLM XML record <a href="download.html?type=nlm&task=${task.id}"> (download)</a>:</p>
        <pre class="brush: xml;">${nlm}</pre>
    </div>

</div>

     