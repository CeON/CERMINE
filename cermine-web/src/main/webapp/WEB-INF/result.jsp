<%-- 
    Document   : result
    Created on : 2012-11-23, 14:07:20
    Author     : Aleksander Nowinski <a.nowinski@icm.edu.pl>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>CERMINE extraction results</title>
    </head>
    <body>
        <h1>CERMINE finished!</h1>
        Message is: ${message}<br/>
        <hr/>
        Processing times:<br/>
        <ul>
            <li>Time in queue: ${result.queueTimeSec}</li>
            <li>Time in queue: ${result.processingTimeSec}</li>
        </ul>
        Result is: <br/>
        <pre>
            ${nlm}
        </pre>
    </body>
</html>
