<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">
     SyntaxHighlighter.all();
</script>


<script>
    $(function() {
        $("#tabs").tabs();
    });
</script>

<h2>Extraction results</h2>

<div id="tabs">
    <ul>
        <li><a href="#tabs-meta">Metadata</a></li>
        <li><a href="#tabs-refs">References</a></li>
        <li><a href="#tabs-text">Full text</a></li>
        <li><a href="#tabs-nlm">NLM</a></li>
    </ul>
    <div id="tabs-meta">
        <p>Extracted metadata formatted in HTML form. Please see NLM for full extraction results.</p>
        <br/>
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
    <div id="tabs-refs">
        <p>Bibliographic references formatted in HTML form. Please see NLM for full extraction result.</p>
        <br/><br/>
        <ol>
        <c:forEach var="reference" items="${meta.references}">
            <li><i>${reference.abstractText}</i></li>
            <ul type="DISC">
            <c:if test='${not empty reference.title}'>
                <li>Title: ${reference.title}</li>
            </c:if>
            <c:forEach var="author" items="${reference.authors}">
                <li>Author: ${author.name}</li>
            </c:forEach>
            <c:if test='${not empty reference.journalTitle}'>
                <li>Journal&nbsp;title: ${reference.journalTitle}</li>
            </c:if>
            <c:if test='${not empty reference.volume}'>
                <li>Volume: ${reference.volume}</li>
            </c:if>    
            <c:if test='${not empty reference.issue}'>
                <li>Issue: ${reference.issue}</li>
            </c:if>    
            <c:if test='${not empty reference.pages}'>
                <li>Pages: ${reference.pages}</li>
            </c:if>    
            <c:if test='${not empty reference.publisher}'>
                <li>Publisher: ${reference.publisher}</li>
            </c:if>
            <c:if test='${not empty reference.publisherLoc}'>
                <li>Publisher&nbsp;location: ${reference.publisherLoc}</li>
            </c:if>
            <c:if test='${not empty reference.pubDate}'>
                <li>Publication&nbsp;date: ${reference.pubDate}</li>
            </c:if>
            </ul><br />
        </c:forEach>
        </ol>
    </div>
    <div id="tabs-text">
        <p>Full text formatted in HTML form. Please see NLM for full extraction result.</p>
        ${html}
    </div>
    <div id="tabs-nlm">
        <p>Result as an NLM XML record <a href="download.html?type=nlm&task=${task.id}"> (download)</a>:</p>
        <pre class="brush: xml;">${nlm}</pre>
    </div>

</div>

     