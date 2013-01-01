<%-- 
    Document   : result
    Created on : 2012-11-23, 14:07:20
    Author     : Aleksander Nowinski <a.nowinski@icm.edu.pl>
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>CERMINE extraction task</title>
        <%@include file="html_meta.jsp" %>
        <c:if test="${!task.finished}">
            <meta http-equiv="refresh" content="5"> 
        </c:if>
    </head>
    <body>
        <div id="wrapper">
            <%@include file="header.jsp" %>
            <article id="main">
                <c:choose>
                    <c:when test="${task.status=='QUEUED'||task.status=='CREATED'}">
                        <h1>Waiting in queue...</h1>
                    </c:when>
                    <c:when test="${task.status=='PROCESSING'}">
                        <h1>Extracting metadata...</h1>
                    </c:when>
                    <c:when test="${task.status=='FINISHED'}">
                        <h1>Extraction finished</h1>
                    </c:when>
                    <c:when test="${task.status=='FAILED'}">
                        <h1>Extraction failed!</h1>
                    </c:when>
                </c:choose>
                <h2>Summary:</h2>
                <table id="task_summary"  class="summaryTable">
                    <tr><th>File:</th><td>${task.fileName}</td></tr>
                    <tr><th>Submitted:</th><td><fmt:formatDate value="${task.creationDate}" pattern="MM/dd/yyyy"/></td></tr>
                    <tr><th>Status:</th><td class="${task.status.css}">${task.status.text}</td></tr>
                    <tr><th>In queue:</th><td><c:if test="${task.finished}">${task.result.queueTimeSec}s</c:if></td></tr>
                    <tr><th>Processing:</th><td><c:if test="${task.finished}">${task.result.processingTimeSec}s</c:if></td></tr>

                    </table>

                <c:if test="${task.failed}">
                    <h2>Oups - something went wrong...</h2>
                    <p>
                        We are sorry, but we were unable to process submitted file <strong>${task.fileName}</strong>. Error message is:
                    </p>

                    <div class="ui-widget">
                        <div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
                            <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                                ${task.result.errorMessage}</p>
                        </div>
                    </div>
                </c:if>
                <c:if test="${task.succeeded}">
                    <%@include file="result.jsp" %>
                </c:if>

            </article>
            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
