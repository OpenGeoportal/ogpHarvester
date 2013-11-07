'use strict';

/* Controllers */

angular.module('ogpHarvester.controllers', []);
angular.module("ogpHarvester.controllers")
	.controller('ManageIngestsCtrl', ['$scope', 'Ingest',
		function($scope, Ingest) {
			$scope.data = {};
			Ingest.query(function(response) {
				$scope.data.ingests = response;
			});
		}
	]);
angular.module('ogpHarvester.controllers')
	.controller('IngestDetailsCtrl', ['$scope', '$routeParams', 'Ingest',
		function($scope, $routeParams, Ingest) {
			// Activate tooltips
			$scope.$on('$viewContentLoaded', function() {
				$(".right-column").tooltip({
					selector: "[data-toggle=tooltip]"
				})
			});


			$scope.params = $routeParams;
			var ingestDetails = Ingest.get({
				id: $scope.params.id
			}, function() {
				$scope.ingestDetails = ingestDetails;
				$scope.totalPassed = {
					count: ingestDetails.passed.restrictedRecords + ingestDetails.passed.publicRecords + ingestDetails.passed.vectorRecords + ingestDetails.passed.rasterRecords
				};
				$scope.totalWarnig = {
					count: ingestDetails.warning.unrequiredFields + ingestDetails.warning.webserviceWarnings
				};
				$scope.totalFailed = {
					count: ingestDetails.error.requiredFields + ingestDetails.error.webServiceErrors + ingestDetails.error.systemErrors
				};
			});
		}
	]);