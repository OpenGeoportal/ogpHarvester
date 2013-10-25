<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="form-group col-md-9">
	<div class="col-md-10">
		<label for="url"> <spring:message
				code="ingestExternalRecords.form.url" />
		</label> <input id="url" name="url" class="form-control col-md-6">

	</div>
</div>

<div class="clearfix"></div>
<div class="form-group multiselect">
	<label for="ogpRepository"><spring:message
			code="ingestExternalRecords.form.ogpRepository" /></label> <select
		multiple="multiple" data-role="multiselect" name="ogpRepository"
		id="ogpRepository" class="form-control">
		<option value="unocha">UN Ocha</option>
		<option value="worldBank">World Bank</option>
		<option value="harvard">Harvard</option>
		<option value="oklahoma">Oklahoma</option>
	</select>
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
				class="glyphicon glyphicon-question-sign black"></span></a> <input
				type="text" class="form-control" id="location" name="location">
		</div>
		<div class="form-group col-md-9">
			<label for="title"><spring:message
					code="ingestExternalRecords.form.title" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.title"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a> <input
				type="text" class="form-control" id="title" name="title">
		</div>
		<div class="form-group col-md-9">
			<label for="subject"><spring:message
					code="ingestExternalRecords.form.subject" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.subject"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<textarea class="form-control" rows="3" name="subject" id="subject"></textarea>
		</div>

		<div class="form-group col-md-9">
			<label for="freeText"><spring:message
					code="ingestExternalRecords.form.freeText" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.freeText"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<textarea class="form-control" rows="3" name="freeText" id="freeText"></textarea>
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
					<input type='text' class="form-control" /> <span
						class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
				<span class="col-md-1 text-center">to</span>
				<div class='input-group date col-md-4' id='rangeTo'>
					<input type='text' class="form-control" /> <span
						class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
			</div>
		</div>

		<div class="form-group col-md-9">
			<label for="customCswQuery"><spring:message
					code="ingestExternalRecords.form.custoCswQuery" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.customCswQuery"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<textarea class="form-control" rows="3" name="customCswQuery"
				id="customCswQuery"></textarea>
		</div>
		<div class="clearfix"></div>

	</div>
</div>
<!-- //search criteria panel-->