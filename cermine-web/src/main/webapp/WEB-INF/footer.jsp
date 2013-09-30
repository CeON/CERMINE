<%@page pageEncoding="UTF-8"%>
<%@ taglib  prefix="c"      uri="http://java.sun.com/jsp/jstl/core"%>

 <footer>
	<section>
		<figure>
                    <a href="http://icm.edu.pl/"><img src="static/images/icm.png" /></a>
                    <a href="http://ceon.pl/"><img src="static/images/ceon.png" /></a>
		</figure>
		<p > CERMINE is a <a href="http://ceon.pl/en/research/">CeON research team</a> project. 
            <a href="http://ceon.pl/">CeON</a> is a part of <a href="http://icm.edu.pl/">ICM</a>.
            cermine-web version: <spring:message code="cermine-web.version"/>, 
            build: <spring:message code="cermine-web.build.timestamp"/></p>
	</section>
</footer>