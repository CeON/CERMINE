<%-- 
    Author     : bart
--%>
<%@ page    language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib  prefix="c"      uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib  prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib  prefix="spring" uri="http://www.springframework.org/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Content ExtRactor and MINEr - User Console</title>
    </head>
    <body>    
     
    <c:out value="${hi}"></c:out>

    <br/>
    cermine-web version:
    <spring:message code="cermine-web.version"/>, build: <spring:message code="cermine-web.build.timestamp"/>
    
    </body>
</html>
