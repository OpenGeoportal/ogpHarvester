<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<html>
<head>
<title>Open GeoPortal Harvester</title>

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


</head>


<body>
	<spring:url value="/webjars/jquery/1.10.2/jquery.js" var="jQuery" />
	<script src="${jQuery}"></script>
	<!-- Latest compiled and minified JavaScript -->
	<spring:url value="/webjars/bootstrap/3.0.0/js/bootstrap.min.js"
		var="bootstrapJs" />
	<script src="${bootstrapJs}"></script>
	<div class="navbar header">
		<div class="container">
			<a href="#" class="navbar-brand"> <img
				src="/web/static/img/header_banner.png" class=".img-responsive" />
			</a>
			<button class="btn btn-default navbar-btn navbar-right">Login</button>
		</div>
	</div>

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
						<li><a href="${adminURL}"><spring:message
									code="sidebar.adminPage" /></a></li>
					</ul>
				</div>
				<!-- //sidebar-nav -->
			</div>
			<!-- // span3 -->

			<div class="col-md-9 right-column">
				<h1>
					<spring:message code="ingestExternalRecords.heading" />
				</h1>
				<spring:message code="ingestExternalRecords.subheading" />

				<h2>
					<spring:message code="ingestExternalRecords.sourceRepository" />
				</h2>
				<form action="" role="form">
					<div class="form-group col-md-9">
						<div class="col-md-6">
							<label for="typeOfInstance"> <spring:message
									code="ingestExternalRecords.form.typeInstance" />
							</label> <select id="typeOfInstance" name="typeOfInstance"
								class="form-control">
								<option value="remoteogp"><spring:message
										code="ingestExternalRecords.form.typeInstance.remoteOGPInstance" /></option>
								<option value="geonetwork"><spring:message
										code="ingestExternalRecords.form.typeInstance.geonetwork" /></option>
								<option value="csw"><spring:message
										code="ingestExternalRecords.form.typeInstance.csw" /></option>
								<option value="webdav"><spring:message
										code="ingestExternalRecords.form.typeInstance.webDAV" /></option>
							</select>
						</div>
					</div>
					<div class="form-group col-md-9">
						<div class="col-md-6">
							<label for="catalogOfServices"> <spring:message
									code="ingestExternalRecords.form.catalogOfServices" />
							</label> <select id="catalogOfServices" name="catalogOfServices"
								class="form-control col-md-6">
								<option value="">Example catalog 1</option>
								<option value="">Example catalog 2</option>
								<option value="">Example catalog 3</option>
								<option value="">Example catalog 4</option>
							</select>
						</div>
					</div>
					<div class="form-group col-md-9">
						<div class="col-md-10">
							<label for="url"> <spring:message
									code="ingestExternalRecords.form.url" />
							</label> <input id="url" name="url" class="form-control col-md-6">

						</div>
					</div>


				</form>

			</div>
		</div>
		<!-- //row-fluid -->
	</div>
	<!-- // container-fluid -->
</body>

</html>