<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator"
	prefix="decorator"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<!DOCTYPE html>
<html>
<head>
<title>Open GeoPortal Harvester</title>

<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Latest compiled and minified CSS -->
<spring:url value="/webjars/bootstrap/3.0.0/css/bootstrap.css"
	var="bootstrapCss" />
<link rel="stylesheet" href="${bootstrapCss}">

<!-- Optional theme -->
<spring:url value="/webjars/bootstrap/3.0.0/css/bootstrap-theme.css"
	var="bootstrapTheme" />
<link rel="stylesheet" href="${bootstrapTheme}">



<spring:url value="/static/css/main.css" var="mainCss" />
<link rel="stylesheet" href="${mainCss}">

<spring:url value="/webjars/jquery/1.10.2/jquery.js" var="jQuery" />
<script src="${jQuery}"></script>
<!-- Latest compiled and minified JavaScript -->
<spring:url value="/webjars/bootstrap/3.0.0/js/bootstrap.min.js"
	var="bootstrapJs" />
<script src="${bootstrapJs}"></script>
<decorator:head />
</head>
<body>
	<jsp:include page="header.jsp" />


	<!-- Container -->
	<div class="container">
		<div class="row">
			<div class="col-md-3">
				<div class="well sidebar-nav">
					<ul class="nav nav-list">
						<spring:url value="/uploadMetadata" var="${uploadMetadataURL}" />
						<spring:url value="/ingestExternal" var="${ingestExternalURL}" />
						<spring:url value="/manageIngests" var="${manageIngestsURL}" />
						<spring:url value="/editMetadata" var="${editMetadataURL}" />
						<spring:url value="/deleteRecords" var="${deleteRecordsURL}" />
						<spring:url value="/admin" var="${adminURL}" />

						<li><a href="${uploadMetadataURL}"> <spring:message
									code="sidebar.uploadMetadata" />
						</a></li>
						<li><a href="${ingestExternalURL}" class="active"><spring:message
									code="sidebar.ingestExternal" /></a></li>
						<li><a href="${manageIngestsURL}"><spring:message
									code="sidebar.manageIngests" /></a></li>
						<li><a href="${editMetadataURL}"><spring:message
									code="sidebar.editMetadata" /></a></li>
						<li><a href="${deleteRecordsURL}"><spring:message
									code="sidebar.deleteRecords" /></a></li>
						<security:authorize ifAllGranted="ROLE_ADMIN">
							<li><a href="${adminURL}"><spring:message
										code="sidebar.adminPage" /></a></li>
						</security:authorize>
					</ul>
				</div>
				<!-- //sidebar-nav -->
			</div>
			<!-- // span3 -->


			<decorator:body />
		</div>
		<!-- //row -->
	</div>
	<!-- //container -->
</body>
</html>