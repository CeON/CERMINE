<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib  prefix="c"      uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="action" value="${param.action}" scope="request" />

<nav>
	<ul>            
                <li><a id="home" href="index.html"><img src="static/images/icons/home<c:if test="${!empty action && action == 'home'}"><c:out value="Green"/></c:if>.png" /><span>CERMINE Home</span></a></li>
		<li><a id="task" href="tasks.html"><img src="static/images/icons/gear<c:if test="${!empty action && action == 'task'}"><c:out value="Green"/></c:if>.png" /><span>Tasks and results</span></a></li>
		<li><a id="about" href="about.html"><img src="static/images/icons/info<c:if test="${!empty action && action == 'about'}"><c:out value="Green"/></c:if>.png" /><span>About CERMINE</span></a></li>
		<li><a id="git" href="https://github.com/CeON/CERMINE"><img src="static/images/icons/git<c:if test="${!empty action && action == 'git'}"><c:out value="Green"/></c:if>.png" /><span>CERMINE at GitHub</span></a></li>
	</ul>
</nav>

