<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<div class="col-md-9 right-column">
		<div class="panel panel-success">
			<div class="panel-heading">
				<spring:message code="ingestScheduleFinal.success.heading" />
			</div>
			<div class="panel-body">
				<spring:message code="ingestScheduleFinal.success.detail" arguments="${nextRunDate}"/>
			</div>
		</div>
	</div>

</body>
</html>