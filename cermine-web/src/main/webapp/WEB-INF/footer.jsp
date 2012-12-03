<%@page pageEncoding="UTF-8"%>
<%@ taglib  prefix="c"      uri="http://java.sun.com/jsp/jstl/core"%>

<div id="footer">

    <table width="100%">
        <tr>
            <td><a href="http://ceon.pl/"><img align="left" src="static/images/ceon.png"/></a></td>
            <td width="90%">
                CERMINE is a <a href="http://ceon.pl/en/research/">CeON research team project.</a><br/>
                is a part of</td>
            <td><a href="http://icm.edu.pl/"><img align="left" src="static/images/icm.png"/></a></td>
        </tr>
    </table>
    <div id="versions">
        cermine-web version: <spring:message code="cermine-web.version"/>, build: <spring:message code="cermine-web.build.timestamp"/>
    </div>    
</div>
