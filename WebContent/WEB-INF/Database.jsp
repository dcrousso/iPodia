<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.sql.Connection" %>
<%
final String dbDriver = "com.mysql.jdbc.Driver";
final String dbURL = "jdbc:mysql://localhost:3306/ipodia";
final String dbUsername = "root";
final String dbPassword = "";

Class.forName(dbDriver);
final Connection dbConnection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
%>
