<%@ include file="/WEB-INF/Session.jsp" %>
<%
session.invalidate();
response.sendRedirect(request.getContextPath() + "/");
%>