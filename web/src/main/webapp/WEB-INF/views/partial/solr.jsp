<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

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
		</div>
		<div class="form-group">
			<label for="themeKeyword"><spring:message
					code="ingestExternalRecords.form.theme" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.theme"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a> <input
				type="text" class="form-control" id="themeKeyword"
				name="themeKeyword">
		</div>
		<div class="form-group">
			<label for="placeKeyword"><spring:message
					code="ingestExternalRecords.form.place" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.place"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a> <input
				type="text" class="form-control" id="placeKeyword"
				name="placeKeyword">
		</div>
		<div class="form-group">
			<label for=topic><spring:message
					code="ingestExternalRecords.form.topic" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.topic"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a> <select
				class="form-control" id="topic" name="topic">
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.none" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.agriculture" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.biology" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.administrative" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.boundaries" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.atmospheric" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.business" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.elevation" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.environment" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.geological" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.health" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.imagery" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.military" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.water" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.locations" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.oceans" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.cadastral" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.cultural" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.facilities" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.transportation" /></option>
				<option value=""><spring:message
						code="ingestExternalRecords.form.topic.option.utilities" /></option>
			</select>
		</div>

		<div class="form-group">
			<label><spring:message
					code="ingestExternalRecords.form.dateRangeContent" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.dataRangeContent"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a>
			<div class="row no-margin">
				<div class='input-group date col-md-5' id='rangeFrom'>
					<input type='text' class="form-control" /> <span
						class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
				<span class="col-md-2 text-center">to</span>
				<div class='input-group date col-md-5' id='rangeTo'>
					<input type='text' class="form-control" /> <span
						class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
			</div>
		</div>
		<div class="form-group">
			<label for="originator"><spring:message
					code="ingestExternalRecords.form.originator" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.originator"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a> <input
				class="form-control" id="originator" name="originator">
		</div>
		<div class="form-group multiselect">
			<label for="dataType"><spring:message
					code="ingestExternalRecords.form.dataType" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.dataType"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a> <select
				class="form-control" id="dataType" name="dataType"
				multiple="multiple" data-role="multiselect">
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
			</select>
		</div>
		<div class="form-group multiselect">
			<label for="dataRepository"><spring:message
					code="ingestExternalRecords.form.dataRepository" /></label> <a href="#"
				data-toggle="tooltip"
				title='<spring:message code="ingestExternalRecords.tooltip.dataRepository"/>'><span
				class="glyphicon glyphicon-question-sign black"></span></a> <select
				class="form-control" id="dataRepository" name="dataRepository"
				data-role="multiselect" multiple="multiple">
				<c:forEach var="i" begin="1" end="10">
					<option value="${i}">Data Repository ${i}</option>
				</c:forEach>
			</select>
		</div>

		<div class="form-group">
			<div class="col-md-12 no-left-padding">
				<div class="checkbox">
					<label> <input type="checkbox" name="excludeRestricted">
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
					<input type='text' class="form-control" /> <span
						class="input-group-addon"><span
						class="glyphicon glyphicon-calendar"></span> </span>
				</div>
				<span class="col-md-2 text-center">to</span>
				<div class='input-group date col-md-5' id='rangeSolrTo'>
					<input type='text' class="form-control" /> <span
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
			<textarea class="form-control" rows="3" name="customSolrQuery"
				id="customSolrQuery"></textarea>

		</div>
	</div>
</div>
<!-- //search criteria panel-->