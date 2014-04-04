<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="form-group col-md-9">
	<div class="col-md-10">
		<label for="url"> <spring:message
				code="ingestExternalRecords.form.url" />
		</label>
		<form:input id="url" path="url" name="url"
			cssClass="form-control col-md-6" />
	</div>
</div>

<div class="clearfix"></div>
<div class="form-group multiselect">
	<label for="ogpRepository"><spring:message
			code="ingestExternalRecords.form.ogpRepository" /></label>
	<form:select multiple="true" data-role="multiselect"
		name="ogpRepository" path="ogpRepository" id="ogpRepository"
		cssClass="form-control">
		<form:option value="unocha">UN Ocha</form:option>
		<form:option value="worldBank">World Bank</form:option>
		<form:option value="harvard">Harvard</form:option>
		<form:option value="oklahoma">Oklahoma</form:option>
	</form:select>
</div>




<div class="custom-panel">
	<div class="custom-panel-heading collapsed" data-toggle="collapse"
		data-target="#collapseSearchCriteria">
		<h2>
			<spring:message code="ingestExternalRecords.searchCriteria" />
			<a href="#" data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.searchCriteria"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a> <span
				class="glyphicon glyphicon-chevron-up pull-right black"></span>
		</h2>
	</div>
	<div class="custom-panel-body collapse in" id="collapseSearchCriteria">
		<div class="form-group col-md-9">
			<label for="location"><spring:message
					code="ingestExternalRecords.form.location" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.location"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:input cssClass="form-control" id="location" path="location"
				name="location" />
		</div>
		<div class="form-group col-md-9">
			<label for="title"><spring:message
					code="ingestExternalRecords.form.title" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.title"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:input cssClass="form-control" id="title" path="title"
				name="title" />
		</div>
		<div class="form-group col-md-9">
			<label for="subject"><spring:message
					code="ingestExternalRecords.form.subject" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.subject"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:textarea cssClass="form-control" rows="3" path="subject"
				id="subject" name="subject" />
		</div>

		<div class="form-group col-md-9">
			<label for="freeText"><spring:message
					code="ingestExternalRecords.form.freeText" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.freeText"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:textarea cssClass="form-control" rows="3" path="freeText"
				id="freeText" name="freeText" />
		</div>
		<div class="clearfix"></div>

		<div class="form-group">
			<label><spring:message
					code="ingestExternalRecords.form.dateRangeContent" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.dataRangeContent"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<div class="row no-margin">
				<div class='input-group date col-md-4' id='rangeFrom'>
					<form:input cssClass="form-control" path="rangeFrom"
						name="rangeFrom" />
					<span class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
				<span class="col-md-1 text-center">to</span>
				<div class='input-group date col-md-4' id='rangeTo'>
					<form:input cssClass="form-control" path="rangeTo" name="rangeTo" />
					<span class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
			</div>
		</div>

		<div class="form-group col-md-9">
			<label for="customCswQuery"><spring:message
					code="ingestExternalRecords.form.customCswQuery" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.customCswQuery"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:textarea cssClass="form-control" rows="3" path="customCswQuery"
				id="customCswQuery" name="customCswQuery" />
		</div>
		<div class="clearfix"></div>

	</div>
</div>
<!-- //search criteria panel-->