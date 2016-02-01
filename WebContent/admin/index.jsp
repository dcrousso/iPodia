<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="pagetype" value="admin"/>
	<jsp:param name="title" value="Admin"/>
</jsp:include>
		<main>
			<h1>Admin</h1>
			
			<form method = "POST">
			Create a new class: <input type="text" name="new-class">
			<input type="submit" value="Submit" />
			</form>
			
			<%
				if (request != null) {
					String input = request.getParameter("new-class");
					if (input != null) {
						Integer classId = Integer.valueOf(input);		
					}
					
					//now run a sql command to create a table in the database for this class 
				}
			%>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>