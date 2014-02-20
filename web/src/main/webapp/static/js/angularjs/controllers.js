(function() {
	'use strict';

	/* Controllers */

	angular.module('ogpHarvester.controllers', []);


	angular.module("ogpHarvester.controllers")
		.controller('ManageIngestsCtrl', ['$scope', '$routeParams', 'IngestPage', 'Ingest', 'ingestMultiform', '$location', '$log',
			'$modal', '$interval',
			function($scope, $routeParams, IngestPage, Ingest, ingestMultiform, $location, $log, $modal, $interval) {
				$scope.data = {};
				ingestMultiform.init();
				$scope.loadData = function() {
					IngestPage.query($routeParams.page, $routeParams.pageSize, function(response) {
						var pageFromRoute = $routeParams.page ? $routeParams.page : 1;
						if (pageFromRoute == response.pageDetails.number) {
							$scope.ingestPage = response;
							$scope.pageSize = response.pageDetails.size;
						}

					});
				};
				$scope.loadData();

				$scope.refreshData = $interval($scope.loadData, 10000);

				// Cancel $interval when controller is destroyed
				$scope.$on('$destroy', function() {
					if ($scope.refreshData) {
						console.log("Refresh data $interval destroyed");
						$interval.cancel($scope.refreshData);
					}
				});


				if ($routeParams.name) {
					$log.info('Ingest "' + $routeParams.name + " has been successfully created");
				}

				$scope.selectPage = function(page) {
					$location.url('/manageIngests?page=' + page + "&pageSize=" + $scope.pageSize);
				};
				$scope.checkLastDate = function(event, index) {
					var ingestReport = $scope.ingestPage.elements[index];
					if (!ingestReport.lastRun) {
						event.preventDefault();
						return false;
					}
				};

				$scope.UnscheduleIngestCtrl = function($scope, $modalInstance, ingestToUnschedule) {
					$scope.ingestToUnschedule = ingestToUnschedule;

					$scope.cancel = function() {
						$modalInstance.dismiss('cancel');
					};

					$scope.unschedule = function() {
						$scope.unscheduleButtonDisabled = true;

						var id = $scope.ingestToUnschedule.id;
						Ingest.unshedule({
								id: id
							},
							function(data) {
								// success callback
								if (data.status === 'SUCCESS') {
									$modalInstance.close();
									$scope.ingestToUnschedule.nextRun = null;
								} else {
									$scope.unscheduleButtonDisable = false;
									$scope.alerts = [];
									$scope.alerts.push({
										type: 'danger',
										msg: data.result.errorCode
									});
								}

							},
							function(reason) {
								// error callback
								$scope.unscheduleButtonDisable = false;
								$scope.alerts = [];
								$scope.alerts.push({
									type: 'danger',
									msg: reason
								});
							}
						);
					};
				};


				$scope.unscheduleIngest = function(ingest) {

					var ingestToUnschedule = ingest;
					var modalInstance = $modal.open({
						templateUrl: 'resources/unscheduleIngest.html',
						controller: $scope.UnscheduleIngestCtrl,
						backdrop: 'static',
						keyboard: false,
						resolve: {
							ingestToUnschedule: function() {
								return ingestToUnschedule;
							}
						}
					});

					modalInstance.result.then(function(result) {
						// $scope.alerts.push({
						// 	type: 'success', 
						// 	msg: $translate("ADMIN.REPO_SUCCESFULLY_DELETED", {name: repoToDelete.value})
						// });
						//$scope.repositoryList.splice(indexToRemove, 1);
					});



				};

				$scope.CancelExecutionCtrl = function($scope, $modalInstance, ingestToCancel) {
					$scope.ingestToCancel = ingestToCancel;
					$scope.stopButtonDisabled = true;

					$scope.cancel = function() {
						$modalInstance.dismiss('cancel');
					};

					$scope.stopIngest = function() {
						$scope.stopButtonDisabled = false;

					};
				};
				$scope.stopIngest = function(ingest) {
					var ingestToStop = ingest;
					var modalInstance = $modal.open({
						templateUrl: 'resources/stopIngest.html',
						controller: $scope.CancelExecutionCtrl,
						backdrop: 'static',
						keyboard: 'false',
						resolve: {
							ingestToCancel: function() {
								return ingestToStop;
							}
						}
					});
				};

			}
		]);
	angular.module('ogpHarvester.controllers')
		.controller('IngestDetailsCtrl', ['$scope', '$routeParams', 'Ingest', '$log',
			function($scope, $routeParams, Ingest, $log) {
				var isSelectedAll = function($event, elementList) {
					var allSelected = elementList !== undefined;
					for (var i = 0; allSelected && (i < elementList.length); i++) {
						if (!elementList[i].isChecked) {
							allSelected = false;
						}
					}
					return allSelected;
				};

				$scope.isSelectedAll = isSelectedAll;

				var selectAll = function($event, elementList) {
					var checkbox = $event.target;
					if (elementList !== undefined) {
						for (var i = 0; i < elementList.length; i++) {
							elementList[i].isChecked = checkbox.checked;
						}
					}
				};

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
						for (var j = 0; j < mergedList.length && !oneSelected; j++) {
							if (mergedList[j].isChecked) {
								oneSelected = true;
							}
						}
					}
					return oneSelected;
				};

				$scope.anySelected = anySelected;

				var downloadMetadata = function(listOfList) {
					$('.downloadMetadata').remove();
					var selected = [];

					for (var i = 0; i < listOfList.requiredFields.length; i++) {
						$.merge(selected, $(listOfList.requiredFields[i]).map(function() {
							if (this.isChecked) {
								return {
									name: 'requiredField',
									value: this.key
								};
							}
						}));
					};

					for (var i = 0; i < listOfList.webserviceError.length; i++) {
						$.merge(selected, $(listOfList.webserviceError[i]).map(function() {
							if (this.isChecked) {
								return {
									name: 'webserviceError',
									value: this.key
								};
							}
						}));
					};

					for (var i = 0; i < listOfList.systemError.length; i++) {
						$.merge(selected, $(listOfList.systemError[i]).map(function() {
							if (this.isChecked) {
								return {
									name: 'systemError',
									value: this.key
								};
							}
						}));
					};
					$log.info(selected);
					var url = "rest/ingests/" + $routeParams.id + "/metadata/" + $scope.ingestDetails.reportId + "?" + $.param(selected);

					// FIXME Bad practice. DOM shouldn't be manipulated in a controller. Please move it to a directive
					$("body").append("<iframe class='downloadMetadata' src='" + url + "' style='display: none;' ></iframe>");
				};

				$scope.downloadMetadata = downloadMetadata;



				$scope.params = $routeParams;
				Ingest.get({
					id: $scope.params.id
				}, function(data) {
					if (data.status === 'SUCCESS') {
						var resp = data.result;
						$scope.ingestDetails = resp;
						$scope.totalPassed = {
							count: resp.passed.restrictedRecords + resp.passed.publicRecords
						};
						$scope.totalWarnig = {
							count: resp.warning.unrequiredFields + resp.warning.webserviceWarnings
						};
						$scope.totalFailed = {
							count: resp.error.requiredFields + resp.error.webServiceErrors + resp.error.systemErrors
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
							$scope.$watch();
						});
					}
				});
			}
		]);

	/** Menu controller */
	angular.module('ogpHarvester.controllers').controller('MenuCtrl', ['$scope', '$location',
		function($scope, $location) {
			/**
			 * @return "active" if baseUrl contains path, otherwise return blank string "".
			 */
			$scope.getClass = function(linkPath) {
				var loc = $location.path();
				if (loc.substr(0, linkPath.length) == linkPath) {
					return "active";
				} else {
					return "";
				}
			};
		}
	]);

	angular.module('ogpHarvester.controllers').controller('NewIngestCtrl', ['$rootScope', '$scope', 'ingestMultiform',
		'remoteRepositories', '$route', '$routeParams', '$location', '$http', '$timeout', '$log', '$modal', '$filter',
		'Ingest', '$translate', '$q',
		function($rootScope, $scope, ingestMultiform, remoteRepositories, $route, $routeParams,
			$location, $http, $timeout, $log, $modal, $filter, Ingest, $translate, $q) {

			if (angular.isUndefined($rootScope.checkBackStep)) {
				$rootScope.checkBackStep = function(angularEvent, next, current) {
					if (next.$$route && next.$$route.originalPath === '/newIngest' &&
						current.$$route.originalPath !== '/newIngest/step2') {
						ingestMultiform.reset();
						ingestMultiform.getIngest().typeOfInstance = "SOLR";
					}
				};
				$rootScope.$on('$routeChangeStart', $rootScope.checkBackStep);

			}
			$scope.alerts = [];
			$scope.urlErrors = [];
			$scope.serviceAlerts = [];
			$scope.closeAlert = function(index) {
				$scope.alerts.splice(index, 1);
			};

			$scope.closeUrlAlerts = function() {
				$scope.urlErrors = [];
			};

			$scope.closeServiceAlerts = function() {
				$scope.urlErrors = [];
			};

			$scope.isTypeOfInstanceDisabled = function() {
				return angular.isDefined($routeParams.id);
			};



			if (angular.isDefined($routeParams.id) && angular.isUndefined($routeParams.back) &&
				$location.$$path.indexOf('/step2') == -1) {

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
					$scope.remoteRepositoriesRequest = remoteRepositories
						.getRemoteSourcesByUrl(repoType, url).success(function(data) {
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


			$scope.dtOpened = {};
			$scope.open = function(identifier) {
				$timeout(function() {
					$scope.dtOpened[identifier] = true;
				});
			};

			$scope.step2 = function() {
				if (angular.isNumber($scope.ingest.id) && $location.path().indexOf('/editIngest') != -1) {
					$location.path('/editIngest/' + $scope.ingest.id + "/step2");
				} else {
					// reset inget id and go to step 2
					if ($scope.ingest) {
						$scope.ingest.id = null;
					}
					ingestMultiform.getIngest().id = null;
					$location.path('/newIngest/step2');
				}

			};

			$scope.goBack = function() {
				if (angular.isNumber($scope.ingest.id) && $location.path().indexOf('/editIngest') != -1) {
					$location.path('/editIngest/' + $scope.ingest.id + '/back');
				} else {
					// reset ingest id and go to step 2
					if ($scope.ingest) {
						$scope.ingest.id = null;
					}
					ingestMultiform.getIngest().id = null;
					$location.path('/newIngest');
				}
			};


			$scope.resetForm = function() {
				$scope.urlErrors = [];
				$scope.serviceAlerts = [];
				$scope.ingest.url = null;
				$scope.ingest.catalogOfServices = null;
				$scope.gnSourcesList = [];
				$scope.solrDataRepositoryList = [];
				$log.log("Starting ingest reset");
				ingestMultiform.reset();
				$log.log("Ended ingest reset");



			};

			$scope.openMap = function() {
				var modalInstance = $modal.open({
					templateUrl: 'resources/map.html',
					controller: MapForm,
					backdrop: 'static',
					keyboard: false
				});

				modalInstance.result.then(function(bbox) {
					$scope.ingest.extent.maxy = bbox.north.toFixed(2);
					$scope.ingest.extent.miny = bbox.south.toFixed(2);
					$scope.ingest.extent.minx = bbox.west.toFixed(2);
					$scope.ingest.extent.maxx = bbox.east.toFixed(2);
				});

			};

			$scope.resetBbox = function() {
				$scope.ingest.extent.maxy = "";
				$scope.ingest.extent.miny = "";
				$scope.ingest.extent.minx = "";
				$scope.ingest.extent.maxx = "";
			};


			/**
			 * Clean URL if no source is selected.
			 */
			$scope.cleanServiceUrl = function() {
				if ($scope.ingest.catalogOfServices != null) {
					$scope.ingest.url = null;
					$scope.urlErrors = [];
					$scope.serviceAlerts = [];
				} else {
					$scope.newIngest.url.$dirty = true;
				}
			};

			$scope.getRemoteSourcesByUrl = function() {
				$scope.urlErrors = [];
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

				if ($scope.remoteRepositoriesPromise != null) {
					$scope.remoteRepositoriesPromise.resolve();
					$scope.remoteRepositoriesPromise = null;
				}

				if (valid && url !== null && url.trim() !== '') {
					$scope.remoteRepositoriesPromise = $q.defer();

					remoteRepositories.getRemoteSourcesByUrl(repoType, url,
						$scope.remoteRepositoriesPromise).success(function(data) {
						if (data.status === "SUCCESS") {
							$scope.ingest[targetModel] = [];
							$scope[targetField] = data.result;
							$scope.urlErrors = [];
						} else {
							$scope.ingest[targetModel] = [];
							$scope[targetField] = [];
							$scope.urlErrors.push($translate("INGEST_FORM." + data.result.errorCode));
						}
					}).error(function() {
						$scope.ingest[targetModel] = [];
						$scope[targetField] = [];
						$scope.urlErrors = [];
						$scope.urlErrors.push($translate("INGEST_FORM.ERROR_RETRIEVING_REMOTE_SOURCES"));
					});
				} else if (!valid || url === null || url === '') {
					$scope.ingest[targetModel] = [];
					$scope[targetField] = [];
				}
			};

			$scope.getRemoteSourcesByRepoId = function() {
				var repoType = $scope.ingest.typeOfInstance;
				var repoId = $scope.ingest.catalogOfServices;
				$scope.serviceAlerts = [];
				if ($scope.servicesPromise != null) {
					$scope.servicesPromise.resolve();
					$scope.servicesPromise = null;
				}
				if (repoId != null) {
					$scope.servicesPromise = $q.defer();
					remoteRepositories.getRemoteSourcesByRepoId(repoId, $scope.servicesPromise).
					success(function(data) {
						if (data.status === "SUCCESS") {
							if (repoType === "SOLR") {
								$scope.ingest.dataRepositories = [];
								$scope.solrDataRepositoryList = data.result;
							} else if (repoType === "GEONETWORK") {
								$scope.ingest.gnSources = [];
								$scope.gnSourcesList = data.result;
							}
						} else {
							$scope.serviceAlerts.push($translate("INGEST_FORM." + data.result.errorCode));
						}
					});
				} else {
					$scope.ingest.dataRepositories = [];
					$scope.solrDataRepositoryList = [];
					$scope.ingest.gnSources = [];
					$scope.gnSourcesList = [];
				}
			};

			$scope.scheduleIngest = function() {
				$log.info("Schedule Ingest");
				var format = "MM/dd/yyyy";
				var ingest = angular.copy($scope.ingest);
				ingest.contentRangeFrom = $filter('date')(ingest.contentRangeFrom, format);
				ingest.contentRangeTo = $filter('date')(ingest.contentRangeTo, format);
				ingest.rangeSolrFrom = $filter('date')(ingest.rangeSolrFrom, format);
				ingest.rangeSolrTo = $filter('date')(ingest.rangeSolrTo, format);
				ingest.cswRangeFrom = $filter('date')(ingest.cswRangeFrom, format);
				ingest.cswRangeTo = $filter('date')(ingest.cswRangeTo, format);
				ingest.webdavFromLastModified = $filter('date')(ingest.webdavFromLastModified, format);
				ingest.webdavToLastModified = $filter('date')(ingest.webdavToLastModified, format);
				ingest.beginDate = $filter('date')(ingest.beginDate, format);
				$http.post("rest/ingests/new", ingest).success(function(data) {
					$log.info("Schedule ingest success: " + JSON.stringify(data));
					$location.path("/manageIngests" + encodeURI("?create=success&name=" + data.data.name));
				}).
				error(function(data, status, headers, config) {
					$log.info("Schedule ingest error");
				});
			};



			remoteRepositories.getRepositoryList().then(
				function(data) {
					$scope.customRepositories = data;
				});

			remoteRepositories.getLocalSolrInstitutions().success(
				function(data) {
					if (data.status === "SUCCESS") {
						$scope.nameOgpRepositoryList = data.result;
						if ($scope.nameOgpRepositoryList && $scope.nameOgpRepositoryList.length > 0 && $scope.nameOgpRepositoryList[0] !== undefined) {
							$scope.ingest.nameOgpRepository = $scope.nameOgpRepositoryList[0].key;
						}
					} else {
						$log.warn("Can not retrieve local Solr institutions list: " + data.result.errorCode);
						$scope.alerts.push({
							type: "danger",
							msg: $translate("INGEST_FORM." + data.result.errorCode)
						});
					}
				}).error(
				function(errorMessage) {
					$scope.error = errorMessage;
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

			$scope.dataTypeList = ["POINT", "LINE", "POLYGON", "RASTER", "SCANNED"];



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

			var MapForm = function($scope, $modalInstance) {
				$scope.bbox = {
					north: "",
					south: "",
					west: "",
					east: ""
				};

				$scope.cancel = function() {
					$modalInstance.dismiss('cancel');
				};

				$scope.setBBOX = function() {
					$log.info("Setting BBOX");

					var fromProjection = new OpenLayers.Projection("EPSG:900913"); // Transform from WGS 1984
					var toProjection = new OpenLayers.Projection("EPSG:4326"); // to Spherical Mercator Projection

					var bounds = map.getExtent().transform(fromProjection, toProjection);
					$scope.bbox.north = bounds.top;
					$scope.bbox.south = bounds.bottom;
					$scope.bbox.west = bounds.left;
					$scope.bbox.east = bounds.right;

					$modalInstance.close($scope.bbox);
				};



				// Public interface
				var publicInterface = {
					initMap: function() {
						$scope.initMap;
					}
				};
				return publicInterface;

			};

		}
	]);


})();