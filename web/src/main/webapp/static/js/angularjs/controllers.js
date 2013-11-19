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
					});
				});

				var isSelectedAll = function ($event, elementList) {
					var allSelected = elementList !== undefined;
					for (var i = 0; allSelected && (i < elementList.length); i++) {
						if (!elementList[i].isChecked) {
							allSelected = false;
						}
					}
					return allSelected;
				};

				$scope.isSelectedAll = isSelectedAll;

				var selectAll = function ($event, elementList) {
					var checkbox = $event.target;
					if (elementList !== undefined) {
						for (var i = 0; i < elementList.length; i++) {
							elementList[i].isChecked = checkbox.checked;
						}
					}
				};

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
						for (var j = 0; j < mergedList.length && !oneSelected; j++) {
							if (mergedList[j].isChecked) {
								oneSelected = true;
							}
						}
					}
					return oneSelected;
				};

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
					var url = "../rest/ingests/" + $routeParams.id + "/metadata?" + $.param(selected);
					$("body").append("<iframe class='downloadMetadata' src='" + url + "' style='display: none;' ></iframe>");
				};

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
						$scope.$watch();

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
			};
		}
	]);

	angular.module('ogpHarvester.controllers').controller('NewIngestCtrl', ['$rootScope', '$scope', 'ingestMultiform',
		'remoteRepositories', '$route', '$location', '$http',

		function ($rootScope, $scope, ingestMultiform, remoteRepositories, $route, $location, $http) {

			$rootScope.$on('$routeChangeStart', function (angularEvent, next, current) {
				if (next.$$route.originalPath === '/newIngest' &&
					current.$$route.originalPath !== '/newIngest/step2') {

					ingestMultiform.reset();
				}


			});

			$scope.step2 = function () {
				// validate ingest

				// go to step 2
				$location.path('/newIngest/step2');

			};

			$scope.resetForm = function () {
				$scope.ingest.url = null;
				$scope.ingest.catalogOfServices = null;
				$scope.gnSourcesList = [];
				$scope.solrDataRepositoryList = [];
			};

			/**
			 * Clean url if no source is selected
			 */
			$scope.cleanServiceUrl = function () {
				if ($scope.ingest.catalogOfServices !== null) {
					$scope.ingest.url = null;
				}
			};

			$scope.getRemoteSourcesByUrl = function () {
				var repoType = $scope.ingest.typeOfInstance;
				var url = $scope.ingest.url;
				var valid = $scope.newIngest.url.$valid;
				var targetField, targetModel;
				if (repoType === 'SOLR') {
					targetField = 'solrDataRepositoryList';
					targetModel = 'dataRepositories';
				} else if (repoType === 'GEONETWORK') {
					targetField = 'gnSourcesList';
					targetModel = 'gnSources';
				} else {
					return;
				}

				if (valid && url !== null && url !== '') {
					remoteRepositories.getRemoteSourcesByUrl(repoType, url).success(function (data) {
						console.log("Updated remote repository list with data " + JSON.stringify(data));
						$scope.ingest[targetModel] = [];
						$scope[targetField] = data;
					}).error(function () {
						$scope.ingest[targetModel] = [];
						$scope[targetField] = [];
					});
				} else if (!valid || url === null || url === '') {
					$scope.ingest[targetModel] = [];
					$scope[targetField] = [];
				}
			};

			$scope.getRemoteSourcesByRepoId = function () {
				var repoType = $scope.ingest.typeOfInstance;
				var repoId = $scope.ingest.catalogOfServices;
				if (repoId !== null) {
					remoteRepositories.getRemoteSourcesByRepoId(repoId).
					success(function (data) {
						console.log("Remote sources by Id " + JSON.stringify(data));
						if (repoType === "SOLR") {
							$scope.ingest.dataRepositories = [];
							$scope.solrDataRepositoryList = data;
						} else if (repoType === "GEONETWORK") {
							$scope.ingest.gnSources = [];
							$scope.gnSourcesList = data;
						}
					});
				} else {
					$scope.ingest.dataRepositories = [];
					$scope.solrDataRepositoryList = [];
					$scope.ingest.gnSources = [];
					$scope.gnSourcesList = [];
				}
			};

			$scope.scheduleIngest = function () {
				console.info("Schedule Ingest");
				$http.post("rest/ingests/new", $scope).success(function (data) {
					console.log("Schedule ingest success: " + data);
				}).
				error(function (data, status, headers, config) {
					console.log("Schedule ingest error");
				});
			}



			remoteRepositories.getRepositoryList().success(
				function (data) {
					$scope.customRepositories = data;
				});

			remoteRepositories.getLocalSolrInstitutions().success(
				function (data) {
					$scope.nameOgpRepositoryList = data;
				});



			$scope.typeOfInstanceList = [{
				value: 'SOLR',
				label: 'TOI_SOLR'
			}, {
				value: 'GEONETWORK',
				label: 'TOI_GN'
			}, {
				value: 'CSW',
				label: 'TOI_CSW'
			}, {
				value: 'WEBDAV',
				label: "TOI_WEBDAV"
			}];

			$scope.topicList = [
				"agriculture", "biology", "administrative", "boundaries", "atmospheric", "business",
				"elevation", "environment", "geological", "health", "imagery", "military", "water",
				"locations", "oceans", "cadastral", "cultural", "facilities", "transportation", "utilities"
			];

			$scope.dataTypeList = ["point", "line", "polygon", "raster", "scannedMap"];



			$scope.requiredFieldList = {
				SOLR: ["extent", "topic", "dataType", "themeKeyword", "dateOfContent", "dataRepository",
					"placeKeyword", "originator", "webServices"
				],
				GEONETWORK: ["extent", "topic", "dataType", "themeKeyword", "dateOfContent", "dataRepository",
					"placeKeyword", "originator"
				],
				CSW: ["extent", "topic", "dataType", "themeKeyword", "dateOfContent", "dataRepository",
					"placeKeyword", "originator"
				],
				WEBDAV: ["extent", "topic", "dataType", "themeKeyword", "dateOfContent", "dataRepository",
					"placeKeyword", "originator"
				]
			};

			$scope.ingest = ingestMultiform.getIngest();


		}
	]);
})();