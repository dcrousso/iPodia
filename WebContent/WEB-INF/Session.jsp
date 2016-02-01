<%@ page import="iPodia.User" %>
<%
if (session.isNew() || session.getAttribute("user") == null)
	session.setAttribute("user", new User());
%>
