<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.util.HashMap"%>
<%@ page import="iPodia.Defaults" %>
<%@ include file="/WEB-INF/Session.jsp" %>
<%@ include file="/WEB-INF/Database.jsp" %>
<%
if (!user.isAuthenticated() || !user.isAdmin()) {
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
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="admin"/>
	<jsp:param name="title" value="<%= className %>"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1>Welcome to <%= className %></h1>
			
			<%
				
				int numWeeks = 0;
			
				PreparedStatement ps = dbConnection.prepareStatement("Select * From class_" + classId);
				ResultSet rs = ps.executeQuery();
				
				String pattern = "(Week)(\\d+)(\\w+)";
				Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
				
				while (rs.next()) {
					String id = rs.getString("id");
					
					Matcher m = p.matcher(id);
					if (m.find()) {
						//group 2 represents the week number in the id
						int week = Integer.valueOf(m.group(2));
						
						//set numWeeks to the max week number found in the database
						if (week > numWeeks) {
							numWeeks = week;
						}
					}
					
				}
			%>
			
			<ul> List of Weeks:
			
				
				<%  for (int i = 1; i <= numWeeks; i ++) { %>	
						<li>
							<a href="${pageContext.request.contextPath}/admin/week?id=${param.id}&num= <%= Integer.toString(i)%>">
							Week <%= i %></a>
						</li>
						
						
				  <% } %>
			
			</ul>
			
			<button type= "button" name ="addWeek" value = "Add Week"> Add week</button>
			
		</main>
		
		<script>
		
			var numOfWeeks = 0;
			
			 Array.prototype.forEach.call(document.querySelectorAll("button[name=\"addWeek\"]"), function(item) {
				item.addEventListener("click", makeNewWeek);
			});
			
		
			
			function makeNewWeek(event) {
				
				var listOfWeeks = this.previousElementSibling;
				var numOfWeeks = listOfWeeks.children.length;
				var link = document.createElement("a");
				link.textContent = "Week " + String (numOfWeeks+1);
				link.setAttribute('href', "${pageContext.request.contextPath}/admin/week?id=${param.id}&num=" +String(numOfWeeks+1));
				
				
				var week = document.createElement("li");
				week.appendChild(link);
				listOfWeeks.appendChild(week);
				
				
				
			}
			
		
		
		</script>
		
			
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
