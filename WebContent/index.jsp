<jsp:include page="/ui/templates/header.jsp">
	<jsp:param name="pagetype" value="home"/>
	<jsp:param name="title" value="Home"/>
</jsp:include>
		<main>
			
			<%@page import= "java.sql.DriverManager" %>
			<%@page import= "java.sql.PreparedStatement" %>
			<%@page import= "java.sql.ResultSet" %>
			<%@page import= "java.sql.SQLException" %>
			<%@page import= "java.sql.Connection" %>
			<%
				String driver = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/ipodia_database";
				String username = "root";
				String databasePassword = "";
				Class.forName(driver);
				Connection connection= DriverManager.getConnection(url,username,databasePassword);
			%>
			
			<h1>iPodia</h1>
			<form method="post">
				<div>
					<label for="email">Email</label>
					<input type="text" name="email" id="email">
					
				</div>
				<div>
					<label for="password">Password</label>
					<input type="password" name="password" id="password">
				</div>
				<input type="submit" value="Submit">
			</form>
			
			<%
				if (request != null) {
					String email = request.getParameter("email");
					String enteredPassword = request.getParameter("password");
					
					//email and enteredPassword will be null when the page first loads with no input
					if (email != null && enteredPassword != null) {
						String queryUser = "SELECT * FROM user_table WHERE email = ?";
						PreparedStatement ps = connection.prepareStatement(queryUser);
						ps.setString(1, email);
						ResultSet rs = ps.executeQuery();
						String actualPassword = "";
						boolean emailValid = false;

						// if a user enters an invalid email address, rs.next() returns false and so won't enter the while loop
						// which is why we set the emailValid flag to true if the user enter a valid email address
						while (rs.next()) {
							emailValid = true;
							actualPassword = rs.getString("password");
						}
						
						//can't just do if enteredPassword.equals(actualPassword) because if the user enters an invalid
						//email and blank password, then both enteredPassword and actualPassword would be empty strings
						if (emailValid) {
							if (enteredPassword.equals(actualPassword)) {
								
							} else {
								System.out.println("valid email but wrong password");
							}
							
						} else {
							System.out.println("invalid email");
						}
					}
				} 			
			%>
			
		</main>
<jsp:include page="/ui/templates/footer.jsp"/>