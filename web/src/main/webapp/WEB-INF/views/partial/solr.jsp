<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>




<div class="form-group col-md-9">
	<div class="col-md-6">
		<label for="catalogOfServices"> <spring:message
				code="ingestExternalRecords.form.catalogOfServices" />
		</label>
		<form:select path="catalogOfServices" id="catalogOfServices"
			cssClass="form-control col-md-6">
			<form:options items="${catalogOfServicesList}" itemLabel="value"
				itemValue="key" />
		</form:select>
	</div>
</div>
<div class="form-group col-md-9">
	<div class="col-md-10">
		<label for="cswUrl"> <spring:message
				code="ingestExternalRecords.form.cswUrl" />
		</label>
		<form:input id="cswUrl" path="cswUrl" cssClass="form-control col-md-6" />

	</div>
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

		<div class="form-group">
			<label><spring:message
					code="ingestExternalRecords.form.extent" /></label>
			<button type="button" class="btn btn-default">
				<span class="glyphicon glyphicon-globe"></span>
			</button>
			<a href="#" data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.extent"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:hidden path="extent" />
		</div>
		<div class="form-group">
			<label for="themeKeyword"><spring:message
					code="ingestExternalRecords.form.theme" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.theme"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:input cssClass="form-control" id="themeKeyword"
				path="themeKeyword" />
		</div>
		<div class="form-group">
			<label for="placeKeyword"><spring:message
					code="ingestExternalRecords.form.place" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.place"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:input cssClass="form-control" id="placeKeyword"
				path="placeKeyword" />
		</div>
		<div class="form-group">
			<label for=topic><spring:message
					code="ingestExternalRecords.form.topic" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.topic"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:select cssClass="form-control" id="topic" path="topic">
				<form:option value="">
					<spring:message code="ingestExternalRecords.form.topic.option.none" />
				</form:option>
				<form:option value="agriculture">
					<spring:message
						code="ingestExternalRecords.form.topic.option.agriculture" />
				</form:option>
				<form:option value="biology">
					<spring:message
						code="ingestExternalRecords.form.topic.option.biology" />
				</form:option>
				<form:option value="administrative">
					<spring:message
						code="ingestExternalRecords.form.topic.option.administrative" />
				</form:option>
				<form:option value="boundaries">
					<spring:message
						code="ingestExternalRecords.form.topic.option.boundaries" />
				</form:option>
				<form:option value="atmospheric">
					<spring:message
						code="ingestExternalRecords.form.topic.option.atmospheric" />
				</form:option>
				<form:option value="business">
					<spring:message
						code="ingestExternalRecords.form.topic.option.business" />
				</form:option>
				<form:option value="elevation">
					<spring:message
						code="ingestExternalRecords.form.topic.option.elevation" />
				</form:option>
				<form:option value="environment">
					<spring:message
						code="ingestExternalRecords.form.topic.option.environment" />
				</form:option>
				<form:option value="geological">
					<spring:message
						code="ingestExternalRecords.form.topic.option.geological" />
				</form:option>
				<form:option value="health">
					<spring:message
						code="ingestExternalRecords.form.topic.option.health" />
				</form:option>
				<form:option value="imagery">
					<spring:message
						code="ingestExternalRecords.form.topic.option.imagery" />
				</form:option>
				<form:option value="military">
					<spring:message
						code="ingestExternalRecords.form.topic.option.military" />
				</form:option>
				<form:option value="water">
					<spring:message
						code="ingestExternalRecords.form.topic.option.water" />
				</form:option>
				<form:option value="locations">
					<spring:message
						code="ingestExternalRecords.form.topic.option.locations" />
				</form:option>
				<form:option value="oceans">
					<spring:message
						code="ingestExternalRecords.form.topic.option.oceans" />
				</form:option>
				<form:option value="cadastral">
					<spring:message
						code="ingestExternalRecords.form.topic.option.cadastral" />
				</form:option>
				<form:option value="cultural">
					<spring:message
						code="ingestExternalRecords.form.topic.option.cultural" />
				</form:option>
				<form:option value="facilities">
					<spring:message
						code="ingestExternalRecords.form.topic.option.facilities" />
				</form:option>
				<form:option value="transportation">
					<spring:message
						code="ingestExternalRecords.form.topic.option.transportation" />
				</form:option>
				<form:option value="utilities">
					<spring:message
						code="ingestExternalRecords.form.topic.option.utilities" />
				</form:option>
			</form:select>
		</div>

		<div class="form-group">
			<label><spring:message
					code="ingestExternalRecords.form.dateRangeContent" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.dataRangeContent"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<div class="row no-margin">
				<div class='input-group date col-md-4' id='rangeFrom'>
					<form:input cssClass="form-control" path="rangeFrom" />
					<span class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
				<span class="col-md-1 text-center">to</span>
				<div class='input-group date col-md-4' id='rangeTo'>
					<form:input cssClass="form-control" path="rangeTo" />
					<span class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
			</div>
		</div>
		<div class="form-group">
			<label for="originator"><spring:message
					code="ingestExternalRecords.form.originator" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.originator"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:input cssClass="form-control" id="originator" path="originator" />
		</div>
		<div class="form-group multiselect">
			<label for="dataType"><spring:message
					code="ingestExternalRecords.form.dataType" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.dataType"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:select path="dataType" multiple="true" id="dataType"
				cssClass="form-control" data-role="multiselect">
				<option value="point"><spring:message
						code="ingestExternalRecords.form.dataType.point" /></option>
				<option value="line"><spring:message
						code="ingestExternalRecords.form.dataType.line" /></option>
				<option value="polygon"><spring:message
						code="ingestExternalRecords.form.dataType.polygon" /></option>
				<option value="raster"><spring:message
						code="ingestExternalRecords.form.dataType.raster" /></option>
				<option value="scannedMap"><spring:message
						code="ingestExternalRecords.form.dataType.scannedMap" /></option>
			</form:select>
		</div>
		<div class="form-group multiselect">
			<label for="dataRepository"><spring:message
					code="ingestExternalRecords.form.dataRepository" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.dataRepository"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:select cssClass="form-control" id="dataRepository"
				path="dataRepository" data-role="multiselect" multiple="true">
				<form:options items="${dataRepositoryList}" itemLabel="value"
					itemValue="key" />
			</form:select>
		</div>

		<div class="form-group">
			<div class="col-md-12 no-left-padding">
				<div class="checkbox">
					<label> <form:checkbox path="excludeRestricted" value="false" />
						<spring:message
							code="ingestExternalRecords.form.excludeRestricted" />
					</label> <a href="#" data-toggle="tooltip"
						title='<spring:message code="ingestExternalRecords.tooltip.excludeRestricted"/>'><span
						class="glyphicon glyphicon-question-sign black"></span></a>
				</div>
			</div>
		</div>

		<div class="form-group">
			<label><spring:message
					code="ingestExternalRecords.form.dateRangeSolr" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.dateRangeSolr"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<div class="row no-margin">
				<div class='input-group date col-md-5' id='rangeSolrFrom'>
					<form:input cssClass="form-control" path="rangeSolrFrom" /> <span
						class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
				<span class="col-md-2 text-center">to</span>
				<div class='input-group date col-md-5' id='rangeSolrTo'>
					<form:input cssClass="form-control" path="rangeSolrTo" /> <span
						class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
			</div>
		</div>

		<div class="form-group">
			<label for="customSolrQuery"><spring:message
					code="ingestExternalRecords.form.customSolrQuery" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.customSolrQuery"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<form:textarea cssClass="form-control" rows="3" path="customSolrQuery"
				id="customSolrQuery" />

		</div>
	</div>
</div>
<!-- //search criteria panel-->

