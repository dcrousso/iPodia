<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="iPodia.Defaults"%>
<%@ include file="/WEB-INF/Session.jsp" %>
<%@ include file="/WEB-INF/Database.jsp"%>
<%
if (!user.isAuthenticated() || !user.isRegistrar()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String classId = request.getParameter("id");
if (classId == null || classId.trim().length() == 0) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String className = user.getClassName(Integer.parseInt(classId));
if (className == null)
	className = classId;

PreparedStatement ps;
ResultSet results;

HashSet<String> enrolledTeachers = new HashSet<String>();
ps = dbConnection.prepareStatement("SELECT * FROM admins");
results = ps.executeQuery();
while (results.next()) {
	String classes = results.getString("classes");
	if (Arrays.asList(classes.split(Defaults.CSV_REGEXP)).contains(classId))
		enrolledTeachers.add(results.getString("email"));
}

HashSet<String> enrolledStudents = new HashSet<String>();
ps = dbConnection.prepareStatement("SELECT * FROM students");
results = ps.executeQuery();
while (results.next()) {
	String classes = results.getString("classes");
	if (Arrays.asList(classes.split(Defaults.CSV_REGEXP)).contains(classId))
		enrolledStudents.add(results.getString("email"));
}

String[] teachersToEnroll = request.getParameterValues("teacher");
if (teachersToEnroll != null) {
	for (String teacher : teachersToEnroll) {
		if (teacher == null || teacher.trim().length() == 0 || enrolledTeachers.contains(teacher))
			continue;

		ps = dbConnection.prepareStatement("SELECT * FROM admins WHERE email = ?");
		ps.setString(1, teacher);
		results = ps.executeQuery();
		while (results.next()) {
			String classes = results.getString("classes");
			if (Arrays.asList(classes.split(Defaults.CSV_REGEXP)).contains(classId))
				continue;

			ps = dbConnection.prepareStatement("UPDATE admins SET classes = ? WHERE email = ?");
			ps.setString(1, classes + (classes.length() > 0 ? ", " : "") + classId);
			ps.setString(2, teacher);
			ps.execute();
		}
	}

	for (String teacher : enrolledTeachers) {
		if (teachersToEnroll != null && Arrays.asList(teachersToEnroll).contains(teacher))
			continue;
	
		ps = dbConnection.prepareStatement("SELECT * FROM admins WHERE email = ?");
		ps.setString(1, teacher);
		results = ps.executeQuery();
		while (results.next()) {
			ps = dbConnection.prepareStatement("UPDATE admins SET classes = ? WHERE email = ?");
			ps.setString(1, results.getString("classes").replaceAll(Defaults.generateClassesRegExp(classId), ""));
			ps.setString(2, teacher);
			ps.execute();
		}
	}
}

String[] studentsToEnroll = request.getParameterValues("student");
if (studentsToEnroll != null) {
	for (String student : studentsToEnroll) {
		if (student == null || student.trim().length() == 0 || enrolledStudents.contains(student))
			continue;

		ps = dbConnection.prepareStatement("SELECT * FROM students WHERE email = ?");
		ps.setString(1, student);
		results = ps.executeQuery();
		while (results.next()) {
			String classes = results.getString("classes");
			if (Arrays.asList(classes.split(Defaults.CSV_REGEXP)).contains(classId))
				continue;

			ps = dbConnection.prepareStatement("UPDATE students SET classes = ? WHERE email = ?");
			ps.setString(1, classes + (classes.length() > 0 ? ", " : "") + classId);
			ps.setString(2, student);
			ps.execute();
		}
	}

	for (String student : enrolledStudents) {
		if (studentsToEnroll != null && Arrays.asList(studentsToEnroll).contains(student))
			continue;
	
		ps = dbConnection.prepareStatement("SELECT * FROM students WHERE email = ?");
		ps.setString(1, student);
		results = ps.executeQuery();
		while (results.next()) {
			ps = dbConnection.prepareStatement("UPDATE students SET classes = ? WHERE email = ?");
			ps.setString(1, results.getString("classes").replaceAll(Defaults.generateClassesRegExp(classId), ""));
			ps.setString(2, student);
			ps.execute();
		}
	}
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
			<h1>Welcome to <%= className %></h1>
			<form method="post">
				<section id="teachers">
<% for (String teacher : enrolledTeachers) { %>
					<input type="text" name="teacher" value="<%= teacher %>">
<% } %>
					<button type="button">Add Teacher</button>
				</section>
				<section id="students">
<% for (String student : enrolledStudents) { %>
					<input type="text" name="teacher" value="<%= student %>">
<% } %>
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
