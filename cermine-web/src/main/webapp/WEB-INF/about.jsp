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
	     
                <jsp:include page="navigation.jsp" >
                    <jsp:param name="action" value="about"/>
                </jsp:include>
	         
	         <div class="content">
	         
	             <h1>About CERMINE - <b>Content ExtRactor and MINEr</b></h1>
                 
                 <p>CERMINE is a comprehensive open source system for extracting metadata and 
                     content from scientific articles in born-digital form. The system is able 
                     to process documents in PDF format and extracts:
                     <ul>
                         <li>document's metadata, including title, authors, affiliations, 
                             abstract, keywords, journal name, volume and issue,</li>
                         <li>parsed bibliographic references</li>
                         <li>the structure of document's sections, section titles and paragraphs.
                     </ul>
                 </p>
                          
                 <p>CERMINE is based on a modular workflow, whose architecture ensures that
                     individual workflow steps can be maintained separately. As a result it
                     is easy to perform evaluation, training, improve or replace one step 
                     implementation without changing other parts of the workflow. Most steps
                     implementations utilize supervised and unsupervised machine-leaning 
                     techniques, which increases the maintainability of the system, as well as 
                     its ability to adapt to new document layouts.</p>
                     
                 <h2>REST service</h2>
                 
                 <p>CERMINE contains a REST service that allows for executing the extraction 
                     process by machines. REST service can be useful for digital libraries 
                     that do not have access to a built-in method for extracting metadata 
                     and content from documents. It can be accessed using cURL tool:</p>
                 <br/>
                 <pre>
$ curl -X POST --data-binary @article.pdf \
  --header "Content-Type: application/binary" -v \
  http://cermine.ceon.pl/extract.do</pre>
                 
                 <h2>How to cite CERMINE</h2>
	             <p>Please cite <a target="_blank" href="http://link.springer.com/article/10.1007%2Fs10032-015-0249-8">the following paper</a>:</p>
                 <br/>
                 <p>Dominika Tkaczyk, Pawel Szostek, Mateusz Fedoryszak, Piotr Jan Dendek and Lukasz Bolikowski.
                     <i>CERMINE: automatic extraction of structured metadata from scientific literature</i>.
                     In International Journal on Document Analysis and Recognition, 2015,
		     vol. 18, no. 4, pp. 317-335, doi: 10.1007/s10032-015-0249-8.</p>
                 <br/>
                 <p>BibTeX:</p>
                 <br/>
                 <pre>
@article{
  author={Tkaczyk, Dominika and Szostek, Pawel and Fedoryszak, Mateusz and Dendek, Piotr Jan and Bolikowski, Lukasz},
  title={CERMINE: automatic extraction of structured metadata from scientific literature},
  journal={International Journal on Document Analysis and Recognition (IJDAR)},
  issn={1433-2833},
  publisher={Springer Berlin Heidelberg},
  year={2015},
  doi={10.1007/s10032-015-0249-8},
  pages={317-335},
  volume={18},
  number={4},
  url={http://dx.doi.org/10.1007/s10032-015-0249-8}
}</pre>
                 
                 <h2>License</h2>
	             <p>CERMINE is licensed under GNU Affero General Public License version 3.</p>
	
                 <h2>Technical details</h2>
                 
                 <img src="static/images/flow.png" width="70%" /><br /><br />
                 
                 <p>CERMINE workflow is composed of four main parts:
                     <ul type="circle">
                         <li>Basic structure extraction takes a PDF file on the input and produces a geometric 
                             hierarchical structure representing the document. The structure is composed of pages, 
                             zones, lines, words and characters. The reading order of all elements is determined. Every 
                             zone is labelled with one of four general categories: METADATA, REFERENCES, BODY and OTHER.
                         <li>Metadata extraction part analyses parts of the geometric hierarchical structure labelled as 
                             METADATA and extracts a rich set of document's metadata from it.
                         <li>References extraction part analyses parts of the geometric hierarchical structure labelled 
                             as REFERENCES and the result is a list of document's parsed bibliographic references.
                         <li>Text extraction part analyses parts of the geometric hierarchical structure labelled as 
                             BODY and extracts document's body structure composed of sections, subsections and 
                             paragraphs.
                     </ul>
                 </p>
                 
                 <p>CERMINE uses supervised and unsupervised machine-leaning techniques, such as Support Vector Machines, 
                     K-means clustering and Conditional Random Fields. Content classifiers are trained on 
                     <a target="_blank" href="http://cermine.ceon.pl/grotoap2/">GROTOAP2 dataset</a>.
                     More information about CERMINE can be found in the <a target="_blank" href="static/docs/slides.pdf">presentation</a>.</p>
                 
			</div>
	
	        </article>
	        <div class="push" ></div>	        
	    </div>
     <%@include file="footer.jsp" %>
</body>
</html>
