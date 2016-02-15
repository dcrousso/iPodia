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
String file = request.getParameter("file");
if (Defaults.isEmpty(classId) || !user.hasClass(classId) || Defaults.isEmpty(week) || Defaults.isEmpty(file)) {
	response.sendError(HttpServletResponse.SC_NOT_FOUND);
	return;
}

File f = new File(Defaults.DATA_DIRECTORY + "/" + classId + "/" + week + "/" + file);
if (!f.exists() || !f.isFile()) {
	response.sendError(HttpServletResponse.SC_NOT_FOUND);
	return;
}

FileInputStream fileStream = new FileInputStream(file);

response.setHeader("Content-Disposition", "inline; filename=" + file);
response.setContentType(Files.probeContentType(f.toPath()));
response.setContentLength((int) file.length());

int byteRead = 0;
while ((byteRead = fileStream.read()) != -1)
	response.getOutputStream().write(byteRead);

response.getOutputStream().flush();
response.getOutputStream().close();
fileStream.close();
%>
