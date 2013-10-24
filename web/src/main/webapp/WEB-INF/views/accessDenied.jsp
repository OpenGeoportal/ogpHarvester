<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
    <title>Open GeoPortal Harvester | Access Denied</title>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Latest compiled and minified CSS -->
    <spring:url value="/webjars/bootstrap/3.0.0/css/bootstrap.css"
                var="bootstrapCss" />
    <link rel="stylesheet" href="${bootstrapCss}">

    <!-- Optional theme -->
    <spring:url value="/webjars/bootstrap/3.0.0/css/bootstrap-theme.css"
                var="bootstrapTheme" />
    <link rel="stylesheet" href="${bootstrapTheme}">

    <spring:url value="/static/css/bootstrap-datetimepicker.min.css"
                var="datetimepicker" />
    <link rel="stylesheet" href="${datetimepicker}">

    <spring:url value="/static/css/bootstrap-multiselect.css"
                var="multiselect" />
    <link rel="stylesheet" href="${multiselect}">


    <spring:url value="/static/css/main.css" var="mainCss" />
    <link rel="stylesheet" href="${mainCss}">
</head>

<body>
    <div class="navbar header">
        <div class="container">
            <spring:url value="/static/img/header_banner.png" var="headerBanner" />
            <a href="#" class="navbar-brand"> <img src="${headerBanner}"
                                                   class=".img-responsive" />
            </a>
        </div>
    </div>

    <div class="container">
        <h3>Access Denied!</h3>
        <p>You do not have permission to access this page.</p>
    </div>
</body>
</html>