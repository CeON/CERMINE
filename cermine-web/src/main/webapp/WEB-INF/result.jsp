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
        <table>
            <tr><th>Journal title:</th><td>${result.meta.journalTitle}</td></tr>
            <tr><th>Article title:</th><td>${result.meta.title}</td></tr>
        </table>
    </div>
    <div id="tabs-nlm">
        <p>Result as an NLM XML record <a href="download.html?type=nlm&task=${task.id}"> (download)</a>:</p>
        <pre class="brush: xml;">${nlm}</pre>
    </div>

</div>

     