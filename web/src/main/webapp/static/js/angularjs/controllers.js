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

			var isSelectedAll = function($event, elementList) {
				var allSelected = elementList !== undefined;
				for (var i = 0; allSelected && (i < elementList.length); i++) {
					if (!elementList[i].isChecked) {
						allSelected = false;
					}
				}
				return allSelected;
			}

			$scope.isSelectedAll = isSelectedAll;

			var selectAll = function($event, elementList) {
				var checkbox = $event.target;
				if (elementList !== undefined) {
					for (var i = 0; i < elementList.length; i++) {
						elementList[i].isChecked = checkbox.checked;
					}
				}
			}
			$scope.selectAll = selectAll;

			var anySelected = function(listOfList) {
				var mergedList = [];
				var oneSelected = false;
				if (listOfList !== undefined) {
					for (var i = 0; i < listOfList.length; i++) {
						if (listOfList[i] !== undefined) {
							$.merge(mergedList, listOfList[i]);
						}
					}
					for (var i = 0; i < mergedList.length && !oneSelected; i++) {
						if (mergedList[i].isChecked) {
							oneSelected = true;
						}
					}
				}
				return oneSelected;
			}
			$scope.anySelected = anySelected;

			var downloadMetadata = function(listOfList) {
				$('.downloadMetadata').remove();
				var selected = [];
				for (var i = 0; i < listOfList.length; i++) {
					$.merge(selected, $(listOfList[i]).map(function() {
						if (this.isChecked) {
							return {
								name: 'categories',
								value: this.key
							};
						}
					}));
				}
				console.log(selected);
				var url = "../rest/ingests/" + $routeParams.id + "/metadata?" + $.param(selected)
 				$("body").append("<iframe class='downloadMetadata' src='" + url + "' style='display: none;' ></iframe>"); 
			}
			$scope.downloadMetadata = downloadMetadata;



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
				$scope.ingestDetails.error.allRequired = false;
				$scope.ingestDetails.error.allWebservice = false;
				$scope.ingestDetails.error.allSystem = false;

				$scope.$watch('ingestDetails.error.allRequired', function(value) {
					angular.forEach($scope.ingestDetails.error.requiredFieldsList, function(requiredField, key) {
						requiredField.isChecked = value;
					});
				});
				angular.forEach($scope.ingestDetails.requiredFieldsList, function(requiredField, key) {
					$scope.$watch()

				});
			});
		}
	]);