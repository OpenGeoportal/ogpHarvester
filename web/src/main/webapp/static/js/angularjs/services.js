(function() {

	'use strict';

	/* Services */
	var servicesModule = angular.module('ogpHarvester.services', ['ngResource']);

	servicesModule.factory('Ingest',
			function($resource) {
		return $resource('rest/ingests/:id', {}, {
			'query': {
				method: 'GET',
				url: 'rest/ingests',
				isArray: true,
				cache: false
			},
			'getDetails': {
				method: 'GET',
				url: 'rest/ingests/:id/details',
				isArray: false
			},
			'unshedule': {
				method: 'GET',
				url: 'rest/ingests/:id/unschedule',
				isArray: false
			},
			'interrupt': {
				method: 'GET',
				url: 'rest/ingests/:id/interrupt',
				isArray: false
			}

		});
	})
	.value('version', '0.1');


	servicesModule.factory('IngestPage',
			function($http) {
		var IngestPage = {};
		var query = function(page, pageSize, callback) {
			if (!page) {
				page = 1;
			}
			if (!pageSize) {
				pageSize = 10;
			}
			$http.get('rest/ingests', {
				params: {
					page: page,
					pageSize: pageSize
				}
			}).success(function(data) {
				IngestPage.pageDetails = data.pageDetails;
				IngestPage.elements = data.elements;
				callback(IngestPage);
				return IngestPage;
			});
		};

		return {
			query: query
		};


	});



	servicesModule.service('ingestMultiform', ['$log',
		function($log) {
		// Private data

		var initBean = function() {
			$log.info("Initiating ingest bean");
			var bean = {
					serverQuery: null, 
					typeOfInstance: 'SOLR',
					catalogOfServices: null,
					nameOgpRepository: null,
					url: null,
					extent: {},
					themeKeyword: null,
					placeKeyword: null,
					topic: null,
					originator: null,
					dataTypes: [],
					dataRepositories: [],
					excludeRestricted: false,
					contentRangeFrom: null,
					contentRangeTo: null,
					rangeSolrFrom: null,
					rangeSolrTo: null,
					requiredFields: {},
					gnTitle: null,
					gnKeyword: null,
					gnAbstractText: null,
					gnFreeText: null,
					gnSources: [],
					cswTitle: null,
					cswSubject: null,
					cswFreeText: null,
					cswRangeFrom: null,
					cswRangeTo: null,
					cswCustomQuery: null,
					solrCustomQuery: null,
					webdavFromLastModified: null,
					webdavToLastModified: null,
					ingestName: null,
					beginDate: null,
					frequency: 'ONCE'
			};
			return bean;
		};

		var ingest = initBean();


		// Public interface
		return {
			init: function() {
				ingest = initBean();
			},

			reset: function() {
				//ingest.typeOfInstance = 'SOLR';
				ingest.serverQuery = null;
				ingest.catalogOfServices = null;
				ingest.nameOgpRepository = null;
				ingest.url = null;
				ingest.extent = {};
				ingest.themeKeyword = null;
				ingest.placeKeyword = null;
				ingest.topic = null;
				ingest.originator = null;
				ingest.dataTypes = [];
				ingest.dataRepositories = [];
				ingest.excludeRestricted = false;
				ingest.contentRangeFrom = null;
				ingest.contentRangeTo = null;
				ingest.rangeSolrFrom = null;
				ingest.rangeSolrTo = null;
				ingest.requiredFields = {};
				ingest.gnTitle = null;
				ingest.gnKeyword = null;
				ingest.gnAbstractText = null;
				ingest.gnFreeText = null;
				ingest.gnSources = [];
				ingest.cswTitle = null;
				ingest.cswSubject = null;
				ingest.cswFreeText = null;
				ingest.cswRangeFrom = null;
				ingest.cswRangeTo = null;
				ingest.cswCustomQuery = null;
				ingest.solrCustomQuery = null;
				ingest.webdavFromLastModified = null;
				ingest.webdavToLastModified = null;
				ingest.ingestName = null;
				ingest.beginDate = null;
				ingest.frequency = 'ONCE';
			},

			getIngest: function() {
				return ingest;
			},
			validate: function() {

			},
			copy: function(sourceIngest) {
				angular.copy(sourceIngest, ingest);
			}
		};
	}
	]);

	servicesModule.service('remoteRepositories', ['$http', '$q',
		function($http, $q) {
		// Private data


		// Public interface
		return {
			getRemoteSourcesByRepoId: function(repoId, canceler) {
				return $http.get('rest/repositories/' + repoId + '/remoteSources', {
					timeout: canceler
				});
			},
			getRemoteSourcesByUrl: function(repoType, repoUrl, canceler) {
				return $http.post('rest/repositoriesbyurl/remoteSources', {
					repoType: repoType,
					repoUrl: repoUrl
				}, {
					timeout: canceler
				});
			},
			getRepositoryList: function() {
				var deferred = $q.defer();

				// Calling Repositories services to fetch repository list
				$http.get('rest/repositories').success(function(data) {
					// Passing data to deferred's resolve function on successful completion
					deferred.resolve(data);
				}).error(function() {
					// Sending a friendly error message in case of failure
					deferred.reject("An error occured while fetching repositories");
				});

				return deferred.promise;
			},
			getLocalSolrInstitutions: function() {
				return $http.get('rest/localSolr/institutions');
			},
			save: function(repository) {
				var deferred = $q.defer();

				$http.post('rest/repositories', repository).success(function(data) {
					deferred.resolve(data);
				}).error(function() {
					deferred.reject("An error occured while creating repository");
				});

				return deferred.promise;
			},
			remove: function(id) {
				var deferred = $q.defer();

				$http.delete('rest/repositories/' + id).success(function(data) {
					deferred.resolve(data);
				}).error(function(data, status, headers, config) {
					if (status == 404) {
						deferred.reject("The repository you are trying to remove does not exist anymore. You can dismiss this window.");
					} else {
						deferred.reject("An error occured while removing the repository");
					}
				});
				return deferred.promise;

			},
			checkScheduledIngests: function(id) {
				var deferred = $q.defer();

				$http.get('rest/repositories/' + id + '/hasScheduledIngests', {
					repoId: id
				}).success(
						function(data) {
							if (data.status === 'SUCCESS') {
								deferred.resolve(data.result);
							} else {
								deferred.reject(data.result);
							}
						}).error(
								function() {
									deferred.reject("An error occured while checking if repository has scheduled ingests");
								});
				return deferred.promise;
			}
		};
	}
	]);

	servicesModule.service('predefinedRepositories', ['$http', '$q',
		function($http, $q) {
		return {
			getPredefinedNotInCustom: function() {
				var deferred = $q.defer();

				$http.get('rest/predefinedRepositories/notInCustomRepos').success(function(data) {
					deferred.resolve(data);
				}).error(function(data, status, headers, config) {
					deferred.reject("Cannot retrieve existing repository list");
				});
				return deferred.promise;

			}
		};

	}
	]);


	servicesModule.service('defaultWorkspaces', ['$http', '$q',
		function($http, $q) {

		// Public interface
		return {
			getWorkspaces: function() {

				return $http.get('rest/defaultWorkspaces/getdefaultworkspaces', {
					// manage response
				});
			},
			save: function(workspaceName) {
				return $http.post('rest/defaultWorkspaces/addworkspace/'+workspaceName);
			},
			remove: function(workspaceName) {
				return $http.post('rest/defaultWorkspaces/removeworkspace/'+workspaceName);
			}
		};
	}
	]);
	
	
	servicesModule.service('uploadMetadata', ['$http', '$q', 'Upload', '$translate',
		function($http, $q, Upload, $translate) {

		// Public interface
		return {
			add: function(download, requiredFields) {
				
				Upload.upload({
					url: 'rest/uploadMetadata/add',
					data: {workspace: download.workspace, dataset: download.dataset, requiredFields : requiredFields, file: download.zipFile},
					method: 'POST'
				}).then(function (resp) {
					
					
				}, function (resp) {
					console.log('Error status: ' + resp.data);
					download.status = $translate("UPLOAD_DATA.CUSTOM", {
						custom : download.status + " - " + resp.data
					});
					
				});

			}
		};
	}
	]);

})();