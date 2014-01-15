(function() {
	'use strict';

	/**
	 * ogpHavester.controllers.editIngestCtrl Module
	 *
	 * Description
	 */
	var editIngestModule = angular.module('ogpHavester.controllers.editIngestCtrl', ['ogpHarvester.services', 'ngRoute',
		'ui.bootstrap', 'pascalprecht.translate'
	]);

	editIngestModule.config(['$routeProvider',
		function($routeProvider) {
			$routeProvider.when('/editIngest/:id', {
				templateUrl: 'resources/newIngestForm.html',
				controller: 'EditIngestCtrl'
			});
			$routeProvider.when('/editIngest/:id/step2', {
				templateUrl: 'resources/newIngestFormStep2.html',
				controller: 'EditIngestCtrl'
			});
			$routeProvider.when('/editIngest/:id/:back', {
				templateUrl: 'resources/newIngestForm.html',
				controller: 'EditIngestCtrl'
			});

		}
	]);

	editIngestModule.controller('EditIngestCtrl', ['$scope', '$routeParams', 'Ingest', 'ingestMultiform', 'remoteRepositories', '$log', '$q',
		function($scope, $routeParams, Ingest, ingestMultiform, remoteRepositories, $log, $q) {
			if (angular.isUndefined($routeParams.back)) {
				$scope.urlErrors = [];
				$scope.closeUrlAlerts = function() {
					$scope.urlErrors = [];
				};
				$scope.serviceAlerts = [];
				$scope.closeServiceAlerts = function() {
					$scope.urlErrors = [];
				};


				Ingest.getDetails({
					id: $routeParams.id
				}, function(data) {
					ingestMultiform.reset();
					ingestMultiform.copy(data);
					$scope.ingest = ingestMultiform.getIngest();
					if ($scope.ingest.url !== null) {
						$scope.getRemoteReposByUrl(data.typeOfInstance, data.url);
					}
					if ($scope.ingest.catalogOfServices !== null) {
						$scope.getRemoteReposByRepoId(data.typeOfInstance, data.catalogOfServices);
					}

				});
			}

			$scope.isSolrCustomQueryFilled = function() {
				var solrCustomQuery = $scope.ingest.solrCustomQuery;
				return $.trim(solrCustomQuery).length > 0;
			};

			$scope.isCswCustomQueryFilled = function() {
				var cswCustomQuery = $scope.ingest.cswCustomQuery;
				return $.trim(cswCustomQuery).length > 0;
			};

			$scope.resetOtherFieldsSolr = function() {
				if ($scope.isSolrCustomQueryFilled()) {
					$scope.ingest.themeKeyword = null;
					$scope.ingest.placeKeyword = null;
					$scope.ingest.topic = null;
					$scope.ingest.contentRangeFrom = null;
					$scope.ingest.contentRangeTo = null;
					$scope.ingest.originator = null;
					$scope.ingest.dataTypes = [];
					$scope.ingest.dataRepositories = [];
					$scope.ingest.excludeRestricted = null;
					$scope.ingest.rangeSolrFrom = null;
					$scope.ingest.rangeSolrTo = null;
				}
			};

			$scope.resetOtherFieldsCsw = function() {
				if ($scope.isCswCustomQueryFilled()) {
					$scope.ingest.cswTitle = null;
					$scope.ingest.cswSubject = null;
					$scope.ingest.cswFreeText = null;
					$scope.ingest.cswRangeFrom = null;
					$scope.ingest.cswRangeTo = null;
				}
			};

			$scope.getRemoteReposByUrl = function(repoType, url) {
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

				if (url !== null && url.trim() !== '') {
					if ($scope.remoteRepositoriesRequest != null) {
						$scope.remoteRepositoriesRequest.reject();

					}
					$scope.remoteRepositoriesRequest = remoteRepositories.getRemoteSourcesByUrl(repoType, url).success(function(data) {
						if (data.status === "SUCCESS") {
							$scope[targetField] = data.result;
						} else {
							$scope[targetField] = [];
							$scope.urlError = $translate("INGEST_FORM." + data.errorCode);
						}
						$scope.remoteRepositoriesRequest = null;
					}).error(function() {
						$scope[targetField] = [];
						$scope.remoteRepositoriesRequest = null;
					});
				}
			};

			$scope.getRemoteReposByRepoId = function(repoType, repoId) {
				$scope.serviceAlerts = [];
				if ($scope.servicesPromise != null) {
					$scope.servicesPromise.resolve();
					$scope.servicesPromise = null;
				}
				if (repoId !== null) {
					$scope.servicesPromise = $q.defer();
					remoteRepositories.getRemoteSourcesByRepoId(repoId, $scope.servicesPromise).
					success(function(data) {
						if (data.status === 'SUCCESS') {
							if (repoType === "SOLR") {
								$scope.solrDataRepositoryList = data.result;
							} else if (repoType === "GEONETWORK") {
								$scope.gnSourcesList = data.result;
							}
						} else {
							$scope.serviceAlerts.push($translate("INGEST_FORM." + data.result.errorCode));
						}
					}).error(function() {
						$scope.serviceAlerts.push($translate("INGEST_FORM.ERROR_RETRIEVING_PREDEFINED_REMOTE_SOURCES"));
					});
				}
			};
		}
	]);


})();