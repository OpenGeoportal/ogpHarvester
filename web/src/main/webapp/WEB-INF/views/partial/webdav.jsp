<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="form-group col-md-9">
	<div class="col-md-10">
		<label for="webDavUrl"> <spring:message
				code="ingestExternalRecords.form.webDavUrl" />
		</label> <form:input id="webDavUrl" path="webDavUrl" cssClass="form-control col-md-6" />
		<spring:url value="/admin" var="adminUrl" />
		<spring:message code="ingestExternalRecords.form.webDavUrl.help.link"
			arguments="${adminUrl}" var="adminUrlLink" />
		<p class="help-block">
			<spring:message code="ingestExternalRecords.form.webDavUrl.help"
				arguments="${adminUrlLink}" />
		</p>
	</div>
</div>

<div class="clearfix"></div>
<div class="form-group multiselect">
	<label for="ogpRepository"><spring:message
			code="ingestExternalRecords.form.ogpRepository" /></label> <form:select
		multiple="true" data-role="multiselect" path="ogpRepository"
		id="ogpRepository" cssClass="form-control">
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
	<div class="custom-panel-body collapse" id="collapseSearchCriteria">
		<div class="form-group">
			<label><spring:message
					code="ingestExternalRecords.form.lastModifiedDate" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.lastModifiedDate"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<div class="row no-margin">
				<div class='input-group date col-md-4'>
					<form:input cssClass="form-control" id='lastModifiedFrom'
						path="lastModifiedFrom" /> <span class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
				<span class="col-md-1 text-center">to</span>
				<div class='input-group date col-md-4'>
					<form:input cssClass="form-control" id='lastModifiedTo'
						path="lastMofifiedTo" /> <span class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
			</div>
		</div>
	</div>
</div>