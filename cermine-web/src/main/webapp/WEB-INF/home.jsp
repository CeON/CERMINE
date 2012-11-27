<%@ page    language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib  prefix="c"      uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib  prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib  prefix="spring" uri="http://www.springframework.org/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

        <!-- blueimp fileUpload css: -->
        <link rel="stylesheet" href="<c:url value='/static/fileupload/css/style.css' />"/>
        <link rel="stylesheet" href="<c:url value='/static/fileupload/css/bootstrap.min.css' />"/>
        <!-- eof blueimp fileUpload css-->

        <title>Content ExtRactor and MINEr - User Console</title>
    </head>
    <body>    

        <h4>Welcome to CERMINE - <b>Content ExtRactor and MINEr</b>
        </h4>

        <blockquote>
            Uploaded file will be used only for metadata extraction, we do not store uploaded files.
            <br/>
            Accepted file format - *.pdf, maximum file size is <strong>5 MB</strong>.
        </blockquote>

        <form method='POST' enctype='multipart/form-data' action='upload.do'>
            File to upload: 
            <input type="file" name="files"/>
            <input type="submit" value="Press"/>
        </form>

        <br/>
        cermine-web version:
        <spring:message code="cermine-web.version"/>, build: <spring:message code="cermine-web.build.timestamp"/>

    </body>
</html>
