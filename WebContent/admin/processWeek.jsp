<%@page import="iPodia.FileUploadServlet"%>
<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isAdmin()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="student"/>
	<jsp:param name="title" value="Student"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>

<% 
	//handles the file upload
	FileUploadServlet fileUpload = new FileUploadServlet();
	fileUpload.doPost(request, response);
	
	
	
	
	

%>


</head>
<body>



</body>
</html>

<jsp:include page="/WEB-INF/templates/footer.jsp"/>