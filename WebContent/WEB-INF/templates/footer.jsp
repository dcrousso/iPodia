<%@ page import="iPodia.Defaults" %>
		<div class="scroll-top"><div class="chevron up"></div></div>
		<div class="scripts">
			<script src="${pageContext.request.contextPath}/ui/js/common.js"></script>
<% if (!Defaults.isEmpty(request.getParameter("pagetype"))) { %>
			<script src="${pageContext.request.contextPath}/ui/js/${param.pagetype}.js"></script>
<% } %>
		</div>
	</body>
</html>
<% Defaults.closeDBConnection(); %>
