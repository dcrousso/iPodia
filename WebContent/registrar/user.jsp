<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="iPodia.Defaults" %>
<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isRegistrar()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String type = request.getParameter("type");
String email = request.getParameter("email");
String firstName = request.getParameter("firstName");
String lastName = request.getParameter("lastName");
String university = request.getParameter("university");
if (!Defaults.isEmpty(type) && !Defaults.isEmpty(email) && !Defaults.isEmpty(firstName) && !Defaults.isEmpty(lastName) && !Defaults.isEmpty(university)) {
	PreparedStatement ps = Defaults.getDBConnection().prepareStatement("INSERT INTO users (email, level, firstName, lastName, password, university, classes) VALUES (?, ?, ?, ?, ?, ?, ?)");
	ps.setString(1, email);
	ps.setString(2, type.toLowerCase());
	ps.setString(3, firstName);
	ps.setString(4, lastName);
	ps.setString(5, Defaults.INITIAL_PASSWORD);
	ps.setString(6, university);
	ps.setString(7, "");
	ps.executeUpdate();

	ps.close();
	Defaults.closeDBConnection();

	response.sendRedirect(request.getContextPath() + user.getHome());
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="registrar"/>
	<jsp:param name="title" value="Create User"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1>Create User</h1>
			<section>
				<form method="post">
					<div class="item">
						<select name="type" id="type">
							<option selected>Student</option>
							<option>Admin</option>
							<option>Registrar</option>
						</select>
						<label for="email">Email</label>
						<input type="email" id="email" name="email" required>
						<label for="firstName">First Name</label>
						<input type="text" id="firstName" name="firstName" required>
						<label for="lastName">Last Name</label>
						<input type="text" id="lastName" name="lastName" required>
						<label for="university">University</label>
						<input type="text" id="university" name="university" required>
					</div>
					<button>Submit</button>
				</form>
			</section>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
