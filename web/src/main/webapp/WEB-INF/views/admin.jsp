<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<html>
<head>
    <spring:url value="/static/css/bootstrap-datetimepicker.min.css"
                var="datetimepicker" />
    <link rel="stylesheet" href="${datetimepicker}">

    <spring:url value="/static/css/bootstrap-multiselect.css"
                var="multiselect" />
    <link rel="stylesheet" href="${multiselect}">
</head>


<body>
<spring:url value="/fragments" var="fragmentsUrl" />


<div class="col-md-9 right-column">
    <h1>
        <spring:message code="admin.heading" />
    </h1>
    <spring:message code="admin.subheading" />

    <h2>
        <spring:message code="admin.existingRepository" />
    </h2>


    <h2>
        <spring:message code="admin.customRepository" />
    </h2>

    <spring:url value="/admin/add" var="formUrl" />
    <form:form action="${formUrl}" method="POST" role="form"
               modelAttribute="customRepositoryFormBean">

    </form:form>


    <h2>
        My Repositories
    </h2>

    <table class="table">
        <thead>
        <tr>
            <th>Repository</th>
            <th>Url</th>
            <th>Remove</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="cr" items="${customRepositories}">
            <tr >
                <td>${cr.name}</td>
                <td>${cr.url}</td>
                <td>X</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>


<spring:url value="/static/js/bootstrap-multiselect.js"
            var="multiselectJS" />
<script type="text/javascript" src="${multiselectJS}"></script>
</body>

</html>