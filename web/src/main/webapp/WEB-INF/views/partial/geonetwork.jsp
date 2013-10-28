<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<div class="form-group col-md-9">
	<label for="geonetworkUrl"> <spring:message
			code="ingestExternalRecords.form.geonetworkUrl" />
	</label>
	<form:input id="geonetworkUrl" path="geonetworkUrl"
		cssClass="form-control" />
</div>

<div class="clearfix"></div>
<div class="form-group multiselect">
	<label for="ogpRepository"><spring:message
			code="ingestExternalRecords.form.ogpRepository" /></label>
	<form:select multiple="true" data-role="multiselect"
		path="ogpRepository" id="ogpRepository" cssClass="form-control">
		<form:option value="unocha">UN Ocha</form:option>
		<form:option value="worldBank">World Bank</form:option>
		<form:option value="harvard">Harvard</form:option>
		<form:option value="oklahoma">Oklahoma</form:option>
	</form:select>
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
				class="glyphicon glyphicon-question-sign black"></span></a> <form:input
				cssClass="form-control" id="title" path="title" />
		</div>
		<div class="form-group col-md-9">
			<label for="keyword"><spring:message
					code="ingestExternalRecords.form.keyword" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.keyword"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a> <form:input
				cssClass="form-control" id="keyword" path="keyword" />
		</div>
		<div class="form-group col-md-9">
			<label for="abstractText"><spring:message
					code="ingestExternalRecords.form.abstractText" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.abstractText"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:textarea cssClass="form-control" rows="3" path="abstractText"
				id="abstractText" />
		</div>
		<div class="form-group col-md-9">
			<label for="freeText"><spring:message
					code="ingestExternalRecords.form.freeText" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.freeText"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:textarea cssClass="form-control" rows="3" path="freeText" id="freeText" />
		</div>
		<div class="clearfix"></div>
		<div class="form-group multiselect">
			<label for="geonetworkSources"><spring:message
					code="ingestExternalRecords.form.geonetworkSources" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.geonetworkSources"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a> <form:select
				multiple="true" data-role="multiselect" path="geonetworkSources"
				id="geonetworkSources" cssClass="form-control" items="${geonetworkSourcesList}" itemLabel="value" itemValue="key">

			</form:select>
		</div>
	</div>
</div>
<!-- //search criteria panel-->

