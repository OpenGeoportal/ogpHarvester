'use strict';

(function () {
	/* Controllers */

	angular.module('ogpHarvester.controllers', []);


	angular.module("ogpHarvester.controllers")
		.controller('ManageIngestsCtrl', ['$scope', 'Ingest',
			function ($scope, Ingest) {
				$scope.data = {};
				Ingest.query(function (response) {
					$scope.data.ingests = response;
				});
			}
		]);
	angular.module('ogpHarvester.controllers')
		.controller('IngestDetailsCtrl', ['$scope', '$routeParams', 'Ingest',
			function ($scope, $routeParams, Ingest) {
				// Activate tooltips
				$scope.$on('$viewContentLoaded', function () {
					$(".right-column").tooltip({
						selector: "[data-toggle=tooltip]"
					})
				});

				var isSelectedAll = function ($event, elementList) {
					var allSelected = elementList !== undefined;
					for (var i = 0; allSelected && (i < elementList.length); i++) {
						if (!elementList[i].isChecked) {
							allSelected = false;
						}
					}
					return allSelected;
				}

				$scope.isSelectedAll = isSelectedAll;

				var selectAll = function ($event, elementList) {
					var checkbox = $event.target;
					if (elementList !== undefined) {
						for (var i = 0; i < elementList.length; i++) {
							elementList[i].isChecked = checkbox.checked;
						}
					}
				}
				$scope.selectAll = selectAll;

				var anySelected = function (listOfList) {
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

				var downloadMetadata = function (listOfList) {
					$('.downloadMetadata').remove();
					var selected = [];
					for (var i = 0; i < listOfList.length; i++) {
						$.merge(selected, $(listOfList[i]).map(function () {
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
				}, function () {
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

					$scope.$watch('ingestDetails.error.allRequired', function (value) {
						angular.forEach($scope.ingestDetails.error.requiredFieldsList, function (requiredField, key) {
							requiredField.isChecked = value;
						});
					});
					angular.forEach($scope.ingestDetails.requiredFieldsList, function (requiredField, key) {
						$scope.$watch()

					});
				});
			}
		]);

	/** Menu controller */
	angular.module('ogpHarvester.controllers').controller('MenuCtrl', ['$scope', '$location',
		function ($scope, $location) {
			$scope.getClass = function (baseUrl, path) {
				var loc = $location.absUrl().substring($location.absUrl().indexOf(baseUrl) + baseUrl.length - 1, $location.absUrl().length);
				if (loc.substr(0, path.length) == path) {
					return "active";
				} else {
					return "";
				}
			}
		}
	]);

	angular.module('ogpHarvester.controllers').controller('NewIngestCtrl', ['$scope', 'ingestMultiform',
		function ($scope, ingestMultiform) {
			$scope.typeOfInstanceList = [{
				value: 'solr',
				label: 'TOI_SOLR'
			}, {
				value: 'geonetwork',
				label: 'TOI_GN'
			}, {
				value: 'csw',
				label: 'TOI_CSW'
			}, {
				value: 'webdav',
				label: "TOI_WEBDAV"
			}];
			$scope.catalogOfServicesList = [{
				id: 1,
				name: 'Example catalog 1'
			}, {
				id: 2,
				name: 'Example catalog 2'
			}, {
				id: 3,
				name: 'Example catalog 3'
			}, {
				id: 4,
				name: 'Example catalog 4'
			}, {
				id: 5,
				name: 'Example catalog 5'
			}, ];
			$scope.topicList = [
				"agriculture", "biology", "administrative", "boundaries", "atmospheric", "business",
				"elevation", "environment", "geological", "health", "imagery", "military", "water",
				"locations", "oceans", "cadastral", "cultural", "facilities", "transportation", "utilities"
			];

			$scope.dataTypeList = ["point", "line", "polygon", "raster", "scannedMap"];
			$scope.dataRepositoryList = [{
				id: 1,
				name: 'Data repository 1'
			}, {
				id: 2,
				name: 'Data repository 2'
			}, {
				id: 3,
				name: 'Data repository 3'
			}, {
				id: 4,
				name: 'Data repository 4'
			}, {
				id: 5,
				name: 'Data repository 5'
			}, {
				id: 6,
				name: 'Data repository 6'
			}];

		$scope.gnSourcesList = [{
				id: 1,
				name: "Geonetwork Repository 1"
			}, {
				id: 2,
				name: "Geonetwork Repository 2"
			}, {
				id: 3,
				name: "Geonetwork Repository 3"
			}, {
				id: 4,
				name: "Geonetwork Repository 4"
			}, {
				id: 5,
				name: "Geonetwork Repository 5"
			}];



			$scope.requiredFieldList = {
				solr: ["extent", "topic", "dataType", "themeKeyword", "dateOfContent", "dataRepository",
					"placeKeyword", "originator", "webServices"
				],
				geonetwork: ["extent", "topic", "dataType", "themeKeyword", "dateOfContent", "dataRepository",
					"placeKeyword", "originator", "editDate"
				],
				csw: ["extent", "topic", "dataType", "themeKeyword", "dateOfContent", "dataRepository",
					"placeKeyword", "originator"
				],
				webdav: ["extent", "topic", "dataType", "themeKeyword", "dateOfContent", "dataRepository",
					"placeKeyword", "originator"
				]
			};

			$scope.ingest = ingestMultiform.getIngest();

			if ($scope.catalogOfServicesList != null &&
				$scope.catalogOfServicesList.length >= 1 &&
				$scope.ingest.catalogOfServices == null) {

				$scope.ingest.catalogOfServices = $scope.catalogOfServicesList[0].id;
			}

		}
	]);
})();