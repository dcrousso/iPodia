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
String weekNumber = request.getParameter("week");
String fileName = request.getParameter("file");
if (classId == null || !user.hasClass(classId) || weekNumber == null || fileName == null) {
	response.sendError(HttpServletResponse.SC_NOT_FOUND);
	return;
}

File file = new File(Defaults.DATA_DIRECTORY + "/" + classId + "/" + weekNumber + "/" + fileName);
if (!file.exists() || !file.isFile()) {
	response.sendError(HttpServletResponse.SC_NOT_FOUND);
	return;
}

FileInputStream fileStream = new FileInputStream(file);

response.setHeader("Content-Disposition", "inline; filename=" + fileName);
response.setContentType(Files.probeContentType(file.toPath()));
response.setContentLength((int) file.length());

int byteRead = 0;
while ((byteRead = fileStream.read()) != -1)
	response.getOutputStream().write(byteRead);

response.getOutputStream().flush();
response.getOutputStream().close();
fileStream.close();
%>
