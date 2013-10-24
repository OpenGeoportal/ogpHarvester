<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
</head>

<body onload='document.f.j_username.focus();'>
	<div class="login-container col-md-6 col-md-offset-3">
		<form name='f' action="<c:url value='j_spring_security_check' />"
			method='POST' class="form-horizontal form-signin">


			<h2>Login</h2>

			<c:if test="${not empty error}">
				<div class="alert alert-danger">
					Your login attempt was not successful, try again.<br /> Cause :
					${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
				</div>
			</c:if>


			<div class="form-group">
				<label for="j_username" class="col-lg-2 control-label"><spring:message
						code="login.form.userFieldLabel" /></label>
				<div class="col-lg-10">
					<input type='text' name='j_username' class="form-control"
						<c:if test="${not empty error}">value='${sessionScope["SPRING_SECURITY_LAST_USERNAME_KEY"]}'</c:if> />
				</div>
			</div>
			<div class="form-group">
				<label for="j_password" class="col-lg-2 control-label"><spring:message
						code="login.form.passwordFieldLabel" /></label>
				<div class="col-lg-10">
					<input type='password' name='j_password' class="form-control" />
				</div>
			</div>



			<div class="form-group">
				<button class="btn btn-lg btn-primary col-md-5" type="submit">
					<span class="glyphicon glyphicon-user"></span>
					<spring:message code="login.form.loginButton" />
				</button>
				<button class="btn btn-lg btn-default col-md-5 col-md-offset-2" type="reset">
					<span class="glyphicon glyphicon-thumbs-down"></span>
					<spring:message code="login.form.resetButton" />
				</button>
			</div>
		</form>
	</div>

</body>
</html>