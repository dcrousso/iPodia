<%@ page import="java.util.HashSet" %>
<%@ page import="iPodia.Defaults"%>
<%@ page import="iPodia.MD5Encryption"%>
<%@ include file="/WEB-INF/Session.jsp" %>
<%@ include file="/WEB-INF/Database.jsp" %>
<%
if (!user.isAuthenticated()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

boolean invalidPassword = false;
String oldPassword = request.getParameter("oldPassword");
String newPassword = request.getParameter("newPassword");
if (!Defaults.isEmpty(oldPassword) && !Defaults.isEmpty(newPassword) && newPassword.equals(request.getParameter("confirmPassword"))) {
	String encryptedPassword = MD5Encryption.encrypt(oldPassword);
	PreparedStatement ps = dbConnection.prepareStatement("SELECT * FROM users WHERE email = ?");
	ps.setString(1, user.getEmail());
	ResultSet results = ps.executeQuery();
	while (results.next()) {
		if (!encryptedPassword.equals(results.getString("password")))
			continue;

		PreparedStatement updatePassword = dbConnection.prepareStatement("UPDATE users SET password = ? WHERE email = ?");
		updatePassword.setString(1, MD5Encryption.encrypt(newPassword));
		updatePassword.setString(2, user.getEmail());
		updatePassword.execute();

		ps.close();
		response.sendRedirect(request.getContextPath() + user.getHome());
		return;
	}
	ps.close();

	// Missing/Not-Matching Password
	invalidPassword = true;
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="home"/>
	<jsp:param name="title" value="Reset Password"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h2>Reset Password</h2>
<% if (invalidPassword) { %>
			<h4>Invalid Password</h4>
<% } %>
			<form method="post">
				<section>
					<label for="oldPassword">Old Password</label>
					<input type="text" name="oldPassword" id="oldPassword">
				</section>
				<section>
					<label for="newPassword">New Password</label>
					<input type="password" name="newPassword" id="newPassword">
					<input type="password" name="confirmPassword" id="confirmPassword">
				</section>
				<button>Submit</button>
			</form>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
