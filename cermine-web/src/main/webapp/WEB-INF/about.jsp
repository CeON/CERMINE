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
	<title>Content ExtRactor and MINEr - about</title>
</head>
<body>
	<div class="wrapper">
	    <%@include file="header.jsp" %>
	    
	     <article id="main">
	     
	     <%@include file="navigation.jsp" %>
	         
	         <div class="content">
	         
	             <h1>About CERMINE - <b>Content ExtRactor and MINEr</b></h1>
                 
                 <p>CERMINE is a comprehensive open source system for extracting metadata and 
                     content from scientific articles in born-digital form. The system is able 
                     to process documents in PDF format and extracts:
                     <ul>
                         <li>document's metadata, including title, authors, affiliations, 
                             abstract, keywords, journal name, volume and issue,</li>
                         <li>parsed bibliographic references</li>
                         <li> the structure of document's sections, section titles and paragraphs.
                     </ul>
                 </p>
                          
                 <p>CERMINE is based on a modular workflow, whose architecture ensures that
                     individual workflow steps can be maintained separately. As a result it
                     is easy to perform evaluation, training, improve or replace one step 
                     implementation without changing other parts of the workflow. Most steps
                     implementations utilize supervised and unsupervised machine-leaning 
                     techniques, which increases the maintainability of the system, as well as 
                     its ability to adapt to new document layouts.</p>
                     
                 <h2>How to cite CERMINE</h2>
	             <p>Please cite the following paper:</p>
                 <br/>
                 <p>D. Tkaczyk, L. Bolikowski, A. Czeczko, and K. Rusek. A modular metadata 
                     extraction system for born-digital articles. In 10th IAPR International
                     Workshop on Document Analysis Systems, pages 11–16, 2012.</p>
                 <br/>
                 <p>BibTeX:</p>
                 <br/>
                 <pre>
@inproceedings{TkaczykBCR2012,
  author = {Tkaczyk, Dominika and Bolikowski, Lukasz and Czeczko, Artur and Rusek, Krzysztof},
  title = {A Modular Metadata Extraction System for Born-Digital Articles},
  booktitle = {10th IAPR International Workshop on Document Analysis Systems},
  year = {2012},
  pages = {11-16}
}
                 </pre>
                 
                 <h2>License</h2>
	             <p>CERMINE is licensed under GNU Affero General Public License version 3.</p>
	
			</div>
	
	        </article>
	        <div class="push" ></div>	        
	    </div>
     <%@include file="footer.jsp" %>
</body>
</html>
