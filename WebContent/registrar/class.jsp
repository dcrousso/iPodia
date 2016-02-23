<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="iPodia.Defaults" %>
<%@ include file="/WEB-INF/Database.jsp" %>
<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isRegistrar()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String classId = request.getParameter("id");
if (Defaults.isEmpty(classId)) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String className = user.getClassName(classId);
if (Defaults.isEmpty(className)) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

PreparedStatement ps;
ResultSet results;

HashSet<User> enrolled = new HashSet<User>();
ps = dbConnection.prepareStatement("SELECT * FROM users");
results = ps.executeQuery();
while (results.next()) {
	String classes = results.getString("classes");
	if (Arrays.asList(classes.split(Defaults.CSV_REGEXP)).contains(classId))
		enrolled.add(new User(results));
}
ps.close();

String[] teachersToEnroll = request.getParameterValues("teacher");
String[] studentsToEnroll = request.getParameterValues("student");
if (teachersToEnroll != null || studentsToEnroll != null) {
	HashSet<String> toEnroll = new HashSet<String>();
	toEnroll.addAll(Defaults.arrayToHashSet(teachersToEnroll));
	toEnroll.addAll(Defaults.arrayToHashSet(studentsToEnroll));
	for (String userToEnroll : toEnroll) {
		if (Defaults.isEmpty(userToEnroll) || enrolled.contains(userToEnroll))
			continue;

		ps = dbConnection.prepareStatement("SELECT * FROM users WHERE email = ?");
		ps.setString(1, userToEnroll);
		results = ps.executeQuery();
		while (results.next()) {
			String classes = results.getString("classes");
			if (Arrays.asList(classes.split(Defaults.CSV_REGEXP)).contains(classId))
				continue;

			PreparedStatement addClass = dbConnection.prepareStatement("UPDATE users SET classes = ? WHERE email = ?");
			addClass.setString(1, classes + (classes.length() > 0 ? ", " : "") + classId);
			addClass.setString(2, userToEnroll);
			addClass.execute();

			PreparedStatement users = dbConnection.prepareStatement("SELECT * FROM users WHERE email = ?");
			users.setString(1, userToEnroll);
			ResultSet isStudent = users.executeQuery();
			while (isStudent.next()) {
				User student = new User(isStudent);
				if (student.isStudent()) {
					dbConnection.prepareStatement("ALTER TABLE class_" + classId + " ADD COLUMN " + student.getSafeEmail() + Defaults.beforeMatching + " varchar(255)").execute();
					dbConnection.prepareStatement("ALTER TABLE class_" + classId + " ADD COLUMN " + student.getSafeEmail() + Defaults.afterMatching + " varchar(255)").execute();
					dbConnection.prepareStatement("ALTER TABLE class_" + classId + "_matching ADD COLUMN " + student.getSafeEmail() + " varchar(255)").execute();
				}
			}
			users.close();
		}
	}
	ps.close();

	for (User userToRemove : enrolled) {
		if (toEnroll.contains(userToRemove.getEmail()))
			continue;

		ps = dbConnection.prepareStatement("SELECT * FROM users WHERE email = ?");
		ps.setString(1, userToRemove.getEmail());
		results = ps.executeQuery();
		while (results.next()) {
			PreparedStatement removeClass = dbConnection.prepareStatement("UPDATE users SET classes = ? WHERE email = ?");
			removeClass.setString(1, results.getString("classes").replaceAll(Defaults.generateClassesRegExp(classId), ""));
			removeClass.setString(2, userToRemove.getEmail());
			removeClass.execute();

			if (userToRemove.isStudent()) {
				dbConnection.prepareStatement("ALTER TABLE class_" + classId + " DROP COLUMN " + userToRemove.getSafeEmail() + Defaults.beforeMatching).execute();
				dbConnection.prepareStatement("ALTER TABLE class_" + classId + " DROP COLUMN " + userToRemove.getSafeEmail() + Defaults.afterMatching).execute();
				dbConnection.prepareStatement("ALTER TABLE class_" + classId + "_matching DROP COLUMN " + userToRemove.getSafeEmail()).execute();
			}
		}
	}
	ps.close();

	response.sendRedirect(request.getContextPath() + "/");
	return;
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="registrar"/>
	<jsp:param name="title" value="<%= className %>"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1><%= className %></h1>
			<form method="post">
				<section id="teachers">
<% for (User teacher : enrolled) { if (teacher.isAdmin()) { %>
					<input type="text" name="teacher" value="<%= teacher.getEmail() %>">
<% } } %>
					<button type="button">Add Teacher</button>
				</section>
				<section id="students">
<% for (User student : enrolled) { if (student.isStudent()) { %>
					<input type="text" name="teacher" value="<%= student.getEmail() %>">
<% } } %>
					<button type="button">Add Student</button>
				</section>
				<button>Submit</button>
			</form>
		</main>
		<script>
			(function() {
				function getFirstEmptyInput(container) {
					for (var i = 0; i < container.children.length; ++i) {
						var child = container.children[i];
						if (child.nodeName === "INPUT" && !child.value)
							return child;
					}
					return null;
				}

				var teachers = document.getElementById("teachers");
				teachers.querySelector("button").addEventListener("click", function(event) {
					var input = getFirstEmptyInput(teachers);
					if (!input) {
						input = document.createElement("input");
						input.type = "text";
						input.name = "teacher";
						teachers.insertBefore(input, this);
					}
					input.focus();
				});

				var students = document.getElementById("students");
				students.querySelector("button").addEventListener("click", function(event) {
					var input = getFirstEmptyInput(students);
					if (!input) {
						input = document.createElement("input");
						input.type = "text";
						input.name = "student";
						students.insertBefore(input, this);
					}
					input.focus();
				});
			})();
		</script>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
