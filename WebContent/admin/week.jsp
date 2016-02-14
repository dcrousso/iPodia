<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<!-- http://stackoverflow.com/questions/19510656/how-to-upload-files-on-server-folder-using-jsp -->

</head>
<body>
	<form method="post" action="uploadFile" enctype="multipart/form-data">
		<%-- <input type="text" name="class" value="${param.className}" hidden>
		<input type="text" name="week" value="${param.week}" hidden> --%>

		<div> Select file to upload: </div>
	    <input type="file" name="fileToUpload" id="fileToUpload">

		<input type="submit" value="File Upload">
	</form>
	


</body>
</html>