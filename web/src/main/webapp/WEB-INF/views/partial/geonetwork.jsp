<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="form-group col-md-9">
	<label for="geonetworkUrl"> <spring:message
			code="ingestExternalRecords.form.geonetworkUrl" />
	</label> <input id="geonetworkUrl" name="geonetworkUrl" class="form-control">
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

<div class="clearfix"></div>

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
	<div class="custom-panel-body collapse" id="collapseSearchCriteria">

		<div class="form-group col-md-9">
			<label for="title"><spring:message
					code="ingestExternalRecords.form.title" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.title"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a> <input
				type="text" class="form-control" id="title" name="title">
		</div>
		<div class="form-group col-md-9">
			<label for="keyword"><spring:message
					code="ingestExternalRecords.form.keyword" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.keyword"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a> <input
				type="text" class="form-control" id="keyword" name="keyword">
		</div>
		<div class="form-group col-md-9">
			<label for="abstractText"><spring:message
					code="ingestExternalRecords.form.abstractText" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.abstractText"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<textarea class="form-control" rows="3" name="abstractText"
				id="abstractText"></textarea>
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
		<div class="form-group multiselect">
			<label for="geonetworkSources"><spring:message
					code="ingestExternalRecords.form.geonetworkSources" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.geonetworkSources"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a> <select
				multiple="multiple" data-role="multiselect" name="geonetworkSources"
				id="geonetworkSources" class="form-control">
				<option value="rep1">Repository 1</option>
				<option value="rep2">Repository 2</option>
				<option value="rep3">Repository 3</option>
				<option value="rep4">Repository 4</option>
				<option value="rep5">Repository 5</option>
			</select>
		</div>
	</div>
</div>
<!-- //search criteria panel-->

