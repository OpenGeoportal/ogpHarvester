<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<head>
<script type="text/javascript">
	$(function() {
		$(".right-column").tooltip({
			selector : "[data-toggle=tooltip]"
		});
	});
</script>
</head>
<body>
	<div ng-view></div>

    <spring:url value="/webjars/openlayers/2.13.1/OpenLayers.light.js"
                var="openLayersJS" />
    <script src="${openLayersJS}"></script>
</body>
