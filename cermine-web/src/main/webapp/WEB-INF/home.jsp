<%@ page    language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib  prefix="c"      uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib  prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib  prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" class="no-js">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<%@include file="html_meta.jsp" %>
<!-- eof blueimp fileUpload css-->

<title>Content ExtRactor and MINEr - User Console</title>

<script>
$(function() {
    $("input[type=submit]")
    	.button();
//		.click(function( event ) {
//			event.preventDefault();
//		});
});
</script>
</head>
<body>
        <div class="wrapper">
            
            <%@include file="header.jsp" %>
            
            <article id="main">
            
            	<%@include file="navigation.jsp" %>
            
            	<div class="content">
            
	            <h1>Welcome to CERMINE - Content ExtRactor and MINEr</h1>
	                
	            <fieldset>
	                
	                <legend>Upload PDF file</legend>
	                
	                <p>Upload an PDF file containing scientific article:</p> 
	                <img src="static/images/icons/upload-8-128-1.png" class="upload-image"/>
	                <c:if test="${not empty warning}">
	                    <div class="ui-widget">
	                    <div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
	                        <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
	                            ${warning}</p>
	                    </div>
	                </div>
	                </c:if>
	                <div id="file_upload_form">
	                    <form method='POST' enctype='multipart/form-data' action='upload.do'>
	                        <input type="file" name="files"/>
	                        <input type="submit" value="Upload"/>
	                    </form>
	                </div>
                
                </fieldset>
                <h2>About the service</h2>
                <p>
                    Upload a PDF file containing a scientific article to extract
                    a metadata. PDF document layout will be analysed and automatic
                    algorithm will attempt to use graphical structure to extract
                    information about: 
                </p>
                <ul>
                    <li>Title of the article</li>
                    <li>Journal information (title etc.)</li>
                    <li>Bibliographic information (vol, issue, page numbers etc.)</li>
                    <li>Authors, affiliations</li>
                    <li>Keywords</li>
                    <li>Abstract</li>
                    <li>Bibliographic reference</li>
                </ul>
           
                <h2>Limitations</h2>
                <p>
                    This is an experimental service, and result may be not accurate.
	                Uploaded file will be used only for metadata extraction, we do not store uploaded files.
	               
	                Accepted file format - *.pdf, maximum file size is <mark>5 MB</mark>.
				</p>
				</div>
            </article>
        	
        	<div class="push" ></div>
        </div>
        <%@include file="footer.jsp" %>
</body>
</html>
