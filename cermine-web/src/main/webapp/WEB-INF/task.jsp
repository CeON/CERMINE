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
        <%@include file="header.jsp" %>
        
        
        <h1>Task is processing!</h1>

        Current task status: ${task.status}<br/>
        Submitted on: ${task.creationDate}

        <c:if test="${task.finished}">
            <%@include file="result.jsp" %>
        </c:if>
            
            
            
        <hr/>
        <%@include file="footer.jsp" %>
    </body>
</html>
