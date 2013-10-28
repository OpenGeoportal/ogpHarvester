<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<html>
<head>
<spring:url value="/static/css/bootstrap-datetimepicker.min.css"
	var="datetimepicker" />
<link rel="stylesheet" href="${datetimepicker}">

<spring:url value="/static/css/bootstrap-multiselect.css"
	var="multiselect" />
<link rel="stylesheet" href="${multiselect}">
</head>


<body>
	<spring:url value="/fragments" var="fragmentsUrl" />
	<script type="text/javascript">
		$(function() {
			$('.date').datetimepicker({
				pickTime : false
			});

			$(".right-column").tooltip({
				selector : "[data-toggle=tooltip]"
			});
		});
		$(function() {
			$("#typeOfInstance")
					.on("change",
	function() {
			var value = $(this).val();
			var url = "${fragmentsUrl}";
			$("#fragment")
				.load(
					url,
					{
						instanceType : value
					},
					function(response, status, xhr) {
						if (status === "success") {
						$(
							"#fragment select[data-role=multiselect]")
							.multiselect();
						$('#fragment .date')
							.datetimepicker(
						{
							pickTime : false
						});
					}
				});
			});

		});
	</script>





	<div class="col-md-9 right-column">
		<h1>
			<spring:message code="ingestExternalRecords.heading" />
		</h1>
		<spring:message code="ingestExternalRecords.subheading" />

		<h2>
			<spring:message code="ingestExternalRecords.sourceRepository" />
		</h2>
		<spring:url value="/" var="formUrl" />
		<form:form action="${formUrl}" method="POST" role="form" modelAttribute="ingestFormBean">
			<div class="form-group col-md-9">
				<div class="col-md-6">
					<label for="typeOfInstance"> <spring:message
							code="ingestExternalRecords.form.typeInstance" />
					</label>
					<form:select id="typeOfInstance" path="typeOfInstance"
						cssClass="form-control">
						<form:option value="solr">
							<spring:message
								code="ingestExternalRecords.form.typeInstance.remoteOGPInstance" />
						</form:option>
						<form:option value="geonetwork">
							<spring:message
								code="ingestExternalRecords.form.typeInstance.geonetwork" />
						</form:option>
						<form:option value="csw">
							<spring:message
								code="ingestExternalRecords.form.typeInstance.csw" />
						</form:option>
						<form:option value="webdav">
							<spring:message
								code="ingestExternalRecords.form.typeInstance.webDAV" />
						</form:option>
					</form:select>
				</div>
			</div>
			<div id="fragment">
				<jsp:include page="partial/webdav.jsp" />
			</div>

			<div class="custom-panel last">
				<div class="custom-panel-heading collapsed" data-toggle="collapse"
					data-target="#requiredFields">
					<h2>
						<spring:message code="ingestExternalRecords.requiredFields" />
						<a href="#" data-toggle="tooltip"
							title='<spring:message code="ingestExternalRecords.tooltip.requiredFields"/>'><span
							class="glyphicon glyphicon-question-sign black"></span></a> <span
							class="glyphicon glyphicon-chevron-up pull-right black"></span>
					</h2>
				</div>
				<div class="custom-panel-body collapse" id="requiredFields">
					<div class="form-group container no-left-padding">
						<div class="col-md-4">
							<div class="checkbox">
								<label> <form:checkbox path="requiredFields"
										id="requiredFields" value="extent"/> <spring:message
										code="ingestExternalRecords.form.requiredFields.extent" /></label>
							</div>
							<div class="checkbox">
								<label><form:checkbox path="requiredFields" value="themeKeyword"/> <spring:message
										code="ingestExternalRecords.form.requiredFields.themeKeyword" /></label>
							</div>
							<div class="checkbox">
								<label><form:checkbox path="requiredFields"
									value="placeKeyword" /> <spring:message
										code="ingestExternalRecords.form.requiredFields.placeKeyword" /></label>
							</div>
							<div class="checkbox">
								<label><form:checkbox path="requiredFields"
									value="webServices" /> <spring:message
										code="ingestExternalRecords.form.requiredFields.webServices" /></label>
							</div>
						</div>
						<div class="col-md-4">
							<div class="checkbox">
								<label><form:checkbox path="requiredFields"
									value="topic" /> <spring:message
										code="ingestExternalRecords.form.requiredFields.topic" /></label>
							</div>
							<div class="checkbox">
								<label><form:checkbox path="requiredFields"
									value="dateOfContent" /> <spring:message
										code="ingestExternalRecords.form.requiredFields.dateOfContent" /></label>
							</div>
							<div class="checkbox">
								<label><form:checkbox path="requiredFields"
									value="originator" /> <spring:message
										code="ingestExternalRecords.form.requiredFields.originator" /></label>
							</div>
						</div>
						<div class="col-md-4">
							<div class="checkbox">
								<label><form:checkbox path="requiredFields"
									value="dataType" /> <spring:message
										code="ingestExternalRecords.form.requiredFields.dataType" /></label>
							</div>
							<div class="checkbox">
								<label><form:checkbox path="requiredFields"
									value="dataRepository" /> <spring:message
										code="ingestExternalRecords.form.requiredFields.dataRepository" /></label>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- //Required fields panel -->


			<div class="form-group col-md-9">
				<button type="submit" class="btn btn-primary">
					Schedule ingest <span class="glyphicon glyphicon-play"></span>
				</button>
			</div>
		</form:form>
	</div>


	<spring:url value="/static/js/bootstrap-datetimepicker.js"
		var="datetimepickerJS" />
	<script type="text/javascript" src="${datetimepickerJS}"></script>
	<spring:url value="/static/js/bootstrap-multiselect.js"
		var="multiselectJS" />
	<script type="text/javascript" src="${multiselectJS}"></script>
</body>

</html>