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
<html ng-app="ogpHarvester">
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
			<div class="col-md-3" ng-controller="MenuCtrl">
				<div class="well sidebar-nav">
					<ul class="nav nav-list">
						<spring:url value="/uploadMetadata" var="uploadMetadataURL" />
						<spring:url value="/ingest" var="ingestExternalURL" />
						<spring:url value="/manageIngests/" var="manageIngestsURL" />
						<spring:url value="/editMetadata" var="editMetadataURL" />
						<spring:url value="/deleteRecords" var="deleteRecordsURL" />
						<spring:url value="/admin" var="adminURL" />
						<spring:url value="/" var="baseUrl" />

						<li><a href="${uploadMetadataURL}" ng-class="getClass('${baseUrl}', '/uploadMetadata')"> <spring:message
									code="sidebar.uploadMetadata" />
						</a></li>
						<li><a href="${ingestExternalURL}" ng-class="getClass('${baseUrl}', '/ingest')" ><spring:message
									code="sidebar.ingestExternal" /></a></li>
						<li><a href="${manageIngestsURL}"  ng-class="getClass('${baseUrl}', '/manageIngests/')"><spring:message
									code="sidebar.manageIngests" /></a></li>
						<li><a href="${editMetadataURL}" ng-class="getClass('${baseUrl}', '/editMetadata')"><spring:message
									code="sidebar.editMetadata" /></a></li>
						<li><a href="${deleteRecordsURL}" ng-class="getClass('${baseUrl}', '/deleteRecords')" ><spring:message
									code="sidebar.deleteRecords" /></a></li>
						<security:authorize ifAllGranted="ROLE_ADMIN">
							<li><a href="${adminURL}" ng-class="getClass('${baseUrl}', '/admin')"><spring:message
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
	<spring:url value="/webjars/angularjs/1.2.0/angular.js"
		var="angularjsUrl" />
	<spring:url value="/webjars/angularjs/1.2.0/angular-route.js"
		var="angularRouteUrl" />
	<spring:url value="/webjars/angularjs/1.2.0/angular-resource.js"
		var="angularResourceUrl" />
	<spring:url value="/static/js/angular-translate/angular-translate.js"
		var="angularTranslateUrl" />
	<spring:url
		value="/static/js/angular-translate-loader-static-files/angular-translate-loader-static-files.js"
		var="angularTranslateLoaderStaticFilesUrl" />
	<spring:url value="/static/js/angularjs/ogpHarvester.js"
		var="ogpHarvesterUrl" />
	<spring:url value="/static/js/angularjs/services.js" var="servicesUrl" />
	<spring:url value="/static/js/angularjs/controllers.js"
		var="controllersUrl" />
	<spring:url value="/static/js/angularjs/filters.js" var="filtersUrl" />
	<spring:url value="/static/js/angularjs/directives.js"
		var="directivesUrl" />
	<script src="${angularjsUrl}"></script>
	<script src="${angularRouteUrl}"></script>
	<script src="${angularResourceUrl}"></script>
	<script src="${angularTranslateUrl}"></script>
	<script src="${angularTranslateLoaderStaticFilesUrl}"></script>
	<script src="${ogpHarvesterUrl}"></script>
	<script src="${servicesUrl}"></script>
	<script src="${controllersUrl}"></script>
	<script src="${filtersUrl}"></script>
	<script src="${directivesUrl}"></script>
</body>
</html>