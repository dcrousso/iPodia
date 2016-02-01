<%@page import="iPodia.MD5Encryption"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Admin post sign up page</title>
</head>
<body>

<%@page import= "java.sql.DriverManager" %>
<%@page import= "java.sql.PreparedStatement" %>
<%@page import= "java.sql.ResultSet" %>
<%@page import= "java.sql.SQLException" %>
<%@page import= "java.sql.Connection" %>
<%@ include file="/WEB-INF/Database.jsp" %>
<% 

	String firstName = request.getParameter("first_name");
	String lastName = request.getParameter("last_name");
	String password = request.getParameter("password");
	String encryptedPassword = MD5Encryption.encrypt(password);
	
	String email = request.getParameter("email");
	String university = request.getParameter("university");
	String classes = request.getParameter("classes");
	
	String query= "INSERT into admins(firstName, lastName, password, email, university, classes) values (?, ?, ?, ?, ?, ?)";
	PreparedStatement ps = dbConnection.prepareStatement(query);
	ps.setString(1, firstName);
	ps.setString(2, lastName);
	ps.setString(3, encryptedPassword);
	ps.setString(4, email);
	ps.setString(5, university);
	ps.setString(6, classes);
	ps.executeUpdate();
%>

</body>
</html>