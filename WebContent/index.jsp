<jsp:include page="/ui/templates/header.jsp">
	<jsp:param name="pagetype" value="home"/>
	<jsp:param name="title" value="Home"/>
</jsp:include>
		<main>
			<h1>iPodia</h1>
			<form method="post">
				<div>
					<label for="username">Username</label>
					<input type="text" name="username" id="username">
					
				</div>
				<div>
					<label for="password">Password</label>
					<input type="password" name="password" id="password">
				</div>
				<input type="submit" value="Submit">
			</form>
		</main>
<jsp:include page="/ui/templates/footer.jsp"/>