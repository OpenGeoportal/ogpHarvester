<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>Open GeoPortal Harvester | Login</title>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Latest compiled and minified CSS -->
    <spring:url value="/webjars/bootstrap/3.0.0/css/bootstrap.css"
                var="bootstrapCss"/>
    <link rel="stylesheet" href="${bootstrapCss}">

    <!-- Optional theme -->
    <spring:url value="/webjars/bootstrap/3.0.0/css/bootstrap-theme.css"
                var="bootstrapTheme"/>
    <link rel="stylesheet" href="${bootstrapTheme}">

    <spring:url value="/static/css/bootstrap-datetimepicker.min.css"
                var="datetimepicker"/>
    <link rel="stylesheet" href="${datetimepicker}">

    <spring:url value="/static/css/bootstrap-multiselect.css"
                var="multiselect"/>
    <link rel="stylesheet" href="${multiselect}">


    <spring:url value="/static/css/main.css" var="mainCss"/>
    <link rel="stylesheet" href="${mainCss}">
</head>

<body onload='document.f.j_username.focus();'>
    <div class="navbar header">
        <div class="container">
            <spring:url value="/static/img/header_banner.png" var="headerBanner"/>
            <a href="#" class="navbar-brand"> <img src="${headerBanner}"
                                                   class=".img-responsive"/>
            </a>
        </div>
    </div>

    <div class="container">
        <h3>Login</h3>

        <c:if test="${not empty error}">
            <div class="error">
                Your login attempt was not successful, try again.<br/> Cause :
                    ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
            </div>
        </c:if>

        <form name='f' action="<c:url value='j_spring_security_check' />" method='POST'>

            <div class="row">
                <div class="span4"><label for="j_username">User: </label><br/><input type='text' name='j_username'
                                                                                     <c:if test="${not empty error}">value='${sessionScope["SPRING_SECURITY_LAST_USERNAME_KEY"]}'</c:if> />
                </div>
            </div>
            <div class="row">
                <div class="span4"><label for="j_password">Password: </label><br/><input type='password' name='j_password'/>
                </div>
            </div>
            <br/>

            <div class="row">
                <div class="span4">
                    <button class="btn btn-default" type="submit">
                        <i class="icon-user icon-white"></i>
                        login
                    </button>
                    <button class="btn btn-warning" type="reset">
                        <i class="icon-thumbs-down icon-white"></i>
                        reset
                    </button>
                </div>
            </div>
        </form>
    </div>

</body>
</html>