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
            <tr><th>Journal&nbsp;title:</th><td>${meta.journalTitle}</td></tr>
            <tr><th>Volume:</th><td>${meta.volume}</td></tr>
            <tr><th>Issue:</th><td>${meta.issue}</td></tr>
            <tr><th>First&nbsp;page:</th><td>${meta.fpage}</td></tr>
            <tr><th>Last&nbsp;page:</th><td>${meta.lpage}</td></tr>
            <tr><th>Abstract:</th><td>${meta.abstractText}</td></tr>
            <tr><th>DOI:</th><td>${meta.doi}</td></tr>
            <tr><th>Authors:</th><td>${meta.authorsString}</td></tr>
            <tr><th>Keywords:</th><td>${meta.keywordsString}</td></tr>
        </table>
    </div>
    <div id="tabs-nlm">
        <p>Result as an NLM XML record <a href="download.html?type=nlm&task=${task.id}"> (download)</a>:</p>
        <pre class="brush: xml;">${nlm}</pre>
    </div>

</div>

     