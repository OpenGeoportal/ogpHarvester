<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
</body>