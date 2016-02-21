<%@ page import="java.io.File" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="iPodia.Defaults" %>
<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String classId = request.getParameter("class");
String week = request.getParameter("week");
String fileName = Defaults.urlDecode(request.getParameter("file"));
if (Defaults.isEmpty(classId) || !user.hasClass(classId) || Defaults.isEmpty(week) || Defaults.isEmpty(fileName)) {
	response.sendError(HttpServletResponse.SC_NOT_FOUND);
	return;
}

File file = new File(Defaults.DATA_DIRECTORY + "/" + classId + "/" + week + "/" + fileName);
if (!file.exists() || !file.isFile()) {
	response.sendError(HttpServletResponse.SC_NOT_FOUND);
	return;
}

response.setHeader("Content-Disposition", "inline; filename=" + fileName);
response.setContentType(Files.probeContentType(file.toPath()));
response.setContentLength((int) file.length());

int byteRead = 0;
FileInputStream fileStream = new FileInputStream(file);
while ((byteRead = fileStream.read()) != -1)
	response.getOutputStream().write(byteRead);

response.getOutputStream().flush();
response.getOutputStream().close();
fileStream.close();
%>
