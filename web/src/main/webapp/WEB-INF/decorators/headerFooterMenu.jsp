<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator"
           prefix="decorator" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://www.springframework.org/security/tags"
           prefix="security" %>
<!DOCTYPE html>
<html data-ng-app="ogpHarvester">
<head>
    <title>Open GeoPortal Harvester</title>

    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Latest compiled and minified CSS -->
    <spring:url value="/webjars/bootstrap/3.0.2/css/bootstrap.min.css"
                var="bootstrapCss"/>
    <link rel="stylesheet" href="${bootstrapCss}">

    <!-- Optional theme -->
    <spring:url value="/webjars/bootstrap/3.0.2/css/bootstrap-theme.min.css"
                var="bootstrapTheme"/>
    <link rel="stylesheet" href="${bootstrapTheme}">

    <spring:url value="/static/css/animate-custom.css" var="animateCss"/>
    <link rel="stylesheet" href="${animateCss}">

    <spring:url value="/static/css/select2.css" var="select2Css"/>
    <spring:url value="/static/css/select2-bootstrap.css" var="select2BootstrapCss"/>
    <link rel="stylesheet" href="${select2Css}">
    <link rel="stylesheet" href="${select2BootstrapCss}">


    <spring:url value="/static/css/main.css" var="mainCss"/>
    <link rel="stylesheet" href="${mainCss}">

	<script src="http://maps.google.com/maps/api/js?v=3&amp;sensor=false"></script>
    <spring:url value="/webjars/jquery/1.10.2/jquery.min.js" var="jQuery"/>
    <script type="application/javascript" src="${jQuery}"></script>
    <!-- Latest compiled and minified JavaScript -->
    <spring:url value="/webjars/bootstrap/3.0.2/js/bootstrap.min.js"
                var="bootstrapJs"/>
    <script type="application/javascript" src="${bootstrapJs}"></script>

    <spring:url value="/static/js/select2/select2.min.js" var="select2Js"/>
    <script type="application/javascript" src="${select2Js}"></script>

    <%--
        <spring:url value="/static/css/bootstrap-multiselect.css"
                    var="multiselect"/>
        <link rel="stylesheet" href="${multiselect}">

        <spring:url value="/static/js/bootstrap-multiselect.js"
                    var="multiselectJS"/>
        <script type="text/javascript" src="${multiselectJS}"></script>--%>

    <decorator:head/>
</head>
<body>
<jsp:include page="header.jsp"/>


<!-- Container -->
<div class="container">
    <div class="row">
        <div class="col-md-3" data-ng-controller="MenuCtrl">
            <div class="well sidebar-nav">
                <ul class="nav nav-list">
                    <spring:url value="/uploadMetadata" var="uploadMetadataURL"/>
                    <spring:url value="/ingest" var="ingestExternalURL"/>
                    <spring:url value="/manageIngests/" var="manageIngestsURL"/>
                    <spring:url value="/editMetadata" var="editMetadataURL"/>
                    <spring:url value="/deleteRecords" var="deleteRecordsURL"/>
                    <spring:url value="/admin" var="adminURL"/>
                    <spring:url value="/" var="baseUrl"/>

                    <li class="disabled"><a
                            data-ng-class="getClass('/uploadMetadata')"> <spring:message
                            code="sidebar.uploadMetadata"/>
                    </a></li>
                   <li><a href="${baseUrl}#/uploadData"
                           data-ng-class="getClass('/uploadData')"><spring:message
                            code="sidebar.uploadData"/></a></li>
                    <li><a href="${baseUrl}#/newIngest"
                           data-ng-class="getClass('/newIngest')"><spring:message
                            code="sidebar.ingestExternal"/></a></li>
                    <li><a href="${baseUrl}#/manageIngests"
                           data-ng-class="getClass('/manageIngests')"><spring:message
                            code="sidebar.manageIngests"/></a></li>
                    <li><a href="${baseUrl}#/manageLayers"
                           data-ng-class="getClass('/manageLayers')"><spring:message
                            code="sidebar.manageLayers"/></a></li>
                    <li class="disabled"><a
                            data-ng-class="getClass('/editMetadata')"><spring:message
                            code="sidebar.editMetadata"/></a></li>
                    <li class="disabled"><a
                            data-ng-class="getClass('/deleteRecords')"><spring:message
                            code="sidebar.deleteRecords"/></a></li>
                    <security:authorize ifAllGranted="ROLE_ADMIN">
                        <li><a href="${baseUrl}#/admin"
                               data-ng-class="getClass('/admin')"><spring:message
                                code="sidebar.adminPage"/></a></li>
                    </security:authorize>
                </ul>
            </div>
            <!-- //sidebar-nav -->
        </div>
        <!-- // span3 -->


        <decorator:body/>
    </div>
    <!-- //row -->
</div>
<!-- //container -->
<spring:url value="/webjars/angularjs/1.2.13/angular.min.js"
            var="angularjsUrl"/>
<spring:url value="/webjars/angularjs/1.2.13/angular-animate.min.js"
            var="angularjsAnimateUrl"/>
<spring:url value="/webjars/angularjs/1.2.13/angular-route.min.js"
            var="angularRouteUrl"/>
<spring:url value="/webjars/angularjs/1.2.13/angular-resource.min.js"
            var="angularResourceUrl"/>
<spring:url value="/static/js/angular-translate/angular-translate.js"
            var="angularTranslateUrl"/>
<spring:url value="/static/js/angular-rcsubmit/rc-form.js"
            var="rcFormUrl"/>
<spring:url value="/static/js/angular-ng-file-upload/ng-file-upload.min.js"
            var="ngFileUrl"/>
<spring:url value="/static/js/angular-ng-cookies/angular-cookies.js"
            var="ngCookiesUrl"/>
<spring:url
        value="/static/js/angular-translate-loader-static-files/angular-translate-loader-static-files.js"
        var="angularTranslateLoaderStaticFilesUrl"/>
<spring:url
        value="/static/js/angular-ui-bootstrap/ui-bootstrap-tpls-0.7.0.js"
        var="angularUiBootstrapUrl"/>
<spring:url value="/static/js/angular-ui-utils/modules/ui-utils.min.js"
            var="uiUtilsUrl"/>

<spring:url value="/static/js/angularjs/ogpHarvester.js"
            var="ogpHarvesterUrl"/>
<spring:url value="/static/js/angularjs/interceptors.js"
            var="interceptorsUrl"/>
<spring:url value="/static/js/angularjs/services.js" var="servicesUrl"/>
<spring:url value="/static/js/angularjs/controllers.js"
            var="controllersUrl"/>
<spring:url value="/static/js/angularjs/adminController.js"
            var="adminControllerUrl"/>
<spring:url value="/static/js/angularjs/manageLayersController.js"
            var="manageLayersUrl"/>
<spring:url value="/static/js/angularjs/uploaddata.js"
            var="uploaddataUrl"/>
<spring:url value="/static/js/angularjs/editIngestController.js"
            var="editIngestControllerUrl"/>
<spring:url value="/static/js/angularjs/filters.js" var="filtersUrl"/>
<spring:url value="/static/js/angularjs/directives.js"
            var="directivesUrl"/>
<spring:url value="/static/js/angular-ui-select2/select2.js" var="angularUiSelect2Url" />
<spring:url value="/webjars/momentjs/2.4.0/min/moment.min.js" var="momentjsUrl"/>
<script type="text/javascript" src="${momentjsUrl}"></script>
<script type="text/javascript" src="${angularjsUrl}"></script>
<!--<script src="${angularjsAnimateUrl}"></script>-->
<script type="text/javascript" src="${angularRouteUrl}"></script>
<script type="text/javascript" src="${angularResourceUrl}"></script>
<script type="text/javascript" src="${angularTranslateUrl}"></script>
<script type="text/javascript" src="${angularTranslateLoaderStaticFilesUrl}"></script>
<script type="text/javascript" src="${rcFormUrl}"></script>
<script type="text/javascript" src="${ngFileUrl}"></script>
<script type="text/javascript" src="${ngCookiesUrl}"></script>
<script type="text/javascript" src="${angularUiBootstrapUrl}"></script>
<script type="text/javascript" src="${uiUtilsUrl}"></script>
<script type="text/javascript" src="${angularUiSelect2Url}"></script>
<script type="text/javascript" src="${ogpHarvesterUrl}"></script>
<script type="text/javascript" src="${interceptorsUrl}"></script>
<script type="text/javascript" src="${servicesUrl}"></script>
<script type="text/javascript" src="${controllersUrl}"></script>
<script type="text/javascript" src="${adminControllerUrl}"></script>
<script type="text/javascript" src="${manageLayersUrl}"></script>
<script type="text/javascript" src="${uploaddataUrl}"></script>
<script type="text/javascript" src="${editIngestControllerUrl}"></script>
<script type="text/javascript" src="${filtersUrl}"></script>
<script type="text/javascript" src="${directivesUrl}"></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/angular-smart-table/2.1.8/smart-table.js"></script>


</body>
</html>