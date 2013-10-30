<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator"
	prefix="decorator"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<!-- header -->
<spring:url value="/login" var="loginPageUrl" />
<div class="navbar header">
	<div class="container">
		<spring:url value="/static/img/header_banner.png" var="headerBanner" />
		<spring:message code="header.logo.alt" var="headerLogoAlt" />
		<a href="#" class="navbar-brand"> <img src="${headerBanner}"
			class="img-responsive" alt="${headerLogoAlt}" />
		</a>
		<security:authorize access="isAnonymous()">
			<a href="${loginPageUrl}"
				class="btn btn-default navbar-btn navbar-right"> <spring:message
					code="header.loginButton" />
			</a>
		</security:authorize>
		<security:authorize access="isAuthenticated()">
			<spring:url value="/myIngests" var="myIngestsUrl" />
			<spring:url value="/j_spring_security_logout" var="logoutUrl" />
			<ul class="nav navbar-nav navbar-right">
				<li class="navbar-text"><p class="navbar-text">
						<spring:message code="header.loggedAsText" />
					</p></li>
				<li class="dropdown">
					<button class="btn btn-default navbar-btn dropdown-toggle"
						data-toggle="dropdown">
						<security:authentication property="principal.username" />
						<b class="caret"></b>
					</button>
					<ul class="dropdown-menu">
						<li><a href="${myIngestsUrl}"><spring:message
									code="header.myIngests" /></a></li>
						<li><a href="${logoutUrl}"><spring:message
									code="header.logout" /></a></li>
					</ul>
				</li>
			</ul>
		</security:authorize>
	</div>
</div>