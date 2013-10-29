<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<spring:url value="/static/css/bootstrap-datetimepicker.min.css"
	var="datetimepicker" />
<link rel="stylesheet" href="${datetimepicker}">
<script type="text/javascript">
	$(function() {
		$('.date').datetimepicker({
			pickTime : false
		});
	});
</script>

</head>
<body>
	<div class="col-md-9 right-column step2">
		<h1>
			<spring:message code="ingestSchedule2.heading" />
		</h1>
		<spring:message code="ingestSchedule2.subheading" />
		<spring:url value="/ingest/startIngest" var="formUrl" />
		<form:form action="${formUrl}" method="POST" role="form"
			modelAttribute="ingestFormBean">
			<div class="fields-container">
				<div class="form-group col-md-9">
					<label for="ingestName"> <spring:message
							code="ingestSchedule2.ingestName" />
					</label>
					<form:input id="ingestName" path="ingestName"
						cssClass="form-control" />
				</div>

				<div class="clearfix"></div>

				<div class="form-group">
					<label><spring:message code="ingestSchedule2.beginIngestOn" /></label>
					<div class="row no-margin">
						<div class='input-group date col-md-4' id='beginDate'>
							<form:input cssClass="form-control" path="beginDate" />
							<span class="input-group-addon"><span
								class="glyphicon glyphicon-calendar"></span> </span>
						</div>
					</div>
				</div>

				<div class="form-group col-md-4">
					<label for="frequency"><spring:message
							code="ingestSchedule2.frequency" /></label>
					<form:select path="frequency" id="frequency"
						cssClass="form-control">
						<form:option value="once">
							<spring:message code="ingestSchedule2.frequency.once" />
						</form:option>
						<form:option value="daily">
							<spring:message code="ingestSchedule2.frequency.daily" />
						</form:option>
						<form:option value="weekly">
							<spring:message code="ingestSchedule2.frequency.weekly" />
						</form:option>
						<form:option value="monthly">
							<spring:message code="ingestSchedule2.frequency.monthly" />
						</form:option>
						<form:option value="yearly">
							<spring:message code="ingestSchedule2.frequency.yearly" />
						</form:option>
					</form:select>
				</div>
				<div class="clearfix"></div>
			</div>
			<div class="form-group">
				<spring:url value="/ingest/step1" var="ingestStep1Url"/>
				<a href="${ingestStep1Url}" class="btn btn-default"><span class="glyphicon glyphicon-chevron-left"></span> 
				<spring:message code="ingestSchedule2.form.backButton" /></a>
				<button type="submit" class="btn btn-primary">
					<spring:message code="ingestSchedule2.form.ingestButton" />
				</button>
			</div>

		</form:form>
	</div>
	<spring:url value="/static/js/bootstrap-datetimepicker.js"
		var="datetimepickerJS" />
	<script type="text/javascript" src="${datetimepickerJS}"></script>
</body>
</html>