<%@ page    language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib  prefix="c"      uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib  prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib  prefix="spring" uri="http://www.springframework.org/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <%@include file="html_meta.jsp" %>
        <!-- eof blueimp fileUpload css-->

        <title>Content ExtRactor and MINEr - User Console</title>
    </head>
    <body>    
        <%@include file="header.jsp" %>
        <article id="main">
            <h1>Welcome to CERMINE - <b>Content ExtRactor and MINEr</b></h1>
            <div id="file_upload_form">
                <form method='POST' enctype='multipart/form-data' action='upload.do'>
                    Upload an PDF file containing scientific article: 
                    <input type="file" name="files"/>
                    <input type="submit" value="Press"/>
                </form>
            </div>
            <h2>About the service</h2>
            <p>
                Upload a PDF file containing a scientific article to extract
                a metadata. PDF document layout will be analysed and automatic
                algorithm will attempt to use graphical structure to extract
                information about:
                <ul>
                    <li>Title of the article</li>
                    <li>Journal information (title etc.)</li>
                    <li>Bibliographic information (vol, issue, page numbers etc.)</li>
                    <li>Authors, affiliations</li>
                    <li>Keywords</li>
                    <li>Abstract</li>
                    <li>Bibliographic reference</li>
                </ul>
            </p>
            <h2>Limitations</h2>
            <p>
                This is an experimental service, and result may be not accurate.
            </p>

            Uploaded file will be used only for metadata extraction, we do not store uploaded files.
            <br/>
            Accepted file format - *.pdf, maximum file size is <strong>5 MB</strong>.

        </article>
        <%@include file="footer.jsp" %>
    </body>
</html>
