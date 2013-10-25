<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="form-group col-md-9">
	<div class="col-md-10">
		<label for="webDavUrl"> <spring:message
				code="ingestExternalRecords.form.webDavUrl" />
		</label> <input id="webDavUrl" name="webDavUrl" class="form-control col-md-6">
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
	<div class="custom-panel-body collapse" id="collapseSearchCriteria">
		<div class="form-group">
			<label><spring:message
					code="ingestExternalRecords.form.lastModifiedDate" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.lastModifiedDate"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<div class="row no-margin">
				<div class='input-group date col-md-4'>
					<input type='text' class="form-control" id='lastModifiedFrom'
						name="lastModifiedFrom" /> <span class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
				<span class="col-md-1 text-center">to</span>
				<div class='input-group date col-md-4'>
					<input type='text' class="form-control" id='lastModifiedTo'
						name="lastMofifiedTo" /> <span class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
			</div>
		</div>
	</div>
</div>