<%-- 
    Document   : result
    Created on : 2012-11-23, 14:07:20
    Author     : Aleksander Nowinski <a.nowinski@icm.edu.pl>
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>CERMINE extraction task</title>
    <%@include file="html_meta.jsp" %>
    <c:if test="${!task.finished}">
        <meta http-equiv="refresh" content="5"> 
    </c:if>
</head>
<body>
    <div class="wrapper">
        <%@include file="header.jsp" %>
     <article id="main">
            
       	<jsp:include page="navigation.jsp" >
            <jsp:param name="action" value="task"/>
        </jsp:include>
       	
       	<div class="content" >
       	
       
       	
         <c:choose>
             <c:when test="${task.status=='QUEUED'||task.status=='CREATED'}">
                 <h1>Waiting in queue...</h1>
             </c:when>
             <c:when test="${task.status=='PROCESSING'}">
                 <h1>Extracting metadata...</h1>
                 <%@include file="loading.jsp" %>
             </c:when>
             <c:when test="${task.status=='FINISHED'}">
                 <h1>Extraction finished</h1>
             </c:when>
             <c:when test="${task.status=='FAILED'}">
                 <h1>Extraction failed!</h1>
             </c:when>
         </c:choose> 
         
         
         
         <h3>Summary:</h3>
         <%--
         <table id="task_summary"  class="summaryTable">
             <tr><th>File:</th><td>${task.fileName}</td></tr>
             <tr><th>Submitted:</th><td><fmt:formatDate value="${task.creationDate}" pattern="MM/dd/yyyy"/></td></tr>
             <tr><th>Status:</th><td class="${task.status.css}">${task.status.text}</td></tr>
             <tr><th>In queue:</th><td><c:if test="${task.finished}">${task.result.queueTimeSec}s</c:if></td></tr>
             <tr><th>Processing:</th><td><c:if test="${task.finished}">${task.result.processingTimeSec}s</c:if></td></tr>

         </table>
          --%>
          
         <section class="summary">
         
         <div><b>File:</b>${task.fileName}</div>
         <div><b>Submitted:</b><fmt:formatDate value="${task.creationDate}" pattern="MM/dd/yyyy"/></div>
         <div><b>In queue:</b><c:if test="${task.finished}">${task.result.queueTimeSec}s</c:if></div>
         <div><b>Processing:</b><c:if test="${task.finished}">${task.result.processingTimeSec}s</c:if></div>
		 <div><b>Status:</b><span class="${task.status.css}">${task.status.text}</span></div>
		 </section>
		
         <c:if test="${task.failed}">
             <h2>Oops - something went wrong...</h2>
             <p>
                 We are sorry, but we were unable to process submitted file <strong>${task.fileName}</strong>. Error message is:
             </p>

             <div class="ui-widget">
                 <div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
                     <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                         ${task.result.errorMessage}</p>
                 </div>
             </div>
             <script type="text/javascript">
                ga('send', 'event', 'result', 'error', '${task.fileName}');
             </script>
         </c:if>
         <c:if test="${task.succeeded}">
             <%@include file="result.jsp" %>
             <script type="text/javascript">
                ga('send', 'event', 'result', 'success', '${task.fileName}');
             </script>
         </c:if>

		</div>
    	</article>
    	<div class="push" ></div>
	</div>
	<%@include file="footer.jsp" %>
</body>
</html>
