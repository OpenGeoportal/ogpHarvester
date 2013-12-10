(function() {
	'use strict';

	angular.module('ogpHavester.controllers.adminCtrl', ['ogpHarvester.services', 'ngRoute', 'ui.bootstrap', 'pascalprecht.translate'])

	.config(['$routeProvider',
		function config($routeProvider) {
			$routeProvider.when('/admin', {
				template: 'resources/admin.html',
				controller: 'AdminCtrl'
			});
		}
	])
		.controller('AdminCtrl', ['$scope', 'remoteRepositories', 'predefinedRepositories',
		                          '$modal', '$log', '$translate',

			function AdminCtrl($scope, remoteRepositories, predefinedRepositories, $modal, $log, $translate) {
			
				$scope.alerts = [];
				$scope.closeAlert = function(index) {
				    $scope.alerts.splice(index, 1);
				};
				

				//  Remove modal dialog controller
				$scope.DeleteRepositoryCtrl  = function($scope, $modalInstance, repoToDelete) {
					$scope.repoToDelete = repoToDelete;
					$scope.deleteButtonDisabled = false;
					$scope.alerts = [];
					$scope.closeAlert = function(index) {
					    $scope.alerts.splice(index, 1);
					};
					
					$scope.cancel = function() {
						$modalInstance.dismiss('cancel');
					};
					
					$scope.deleteRepo = function() {
						$scope.deleteButtonDisabled = true;
						var id = $scope.repoToDelete.key;
						remoteRepositories.remove(id).then(function() {
							$modalInstance.close();
						},
						function(reason) {
							$scope.deleteButtonDisabled = false;
							$scope.alerts = [];
							$scope.alerts.push({type:'danger', msg:reason});
						});
						
					};
					
				};
				$scope.deleteRepo = function(index) {
					var indexToRemove = index;
					var repoToDelete = $scope.repositoryList[index];
					var modalInstance = $modal.open({
						templateUrl: 'resources/removeRepository.html',
						controller: $scope.DeleteRepositoryCtrl,
						backdrop: 'static',
						keyboard: false,
						resolve: {
							repoToDelete: function() {
								return repoToDelete;
							}
						}
					});
					
					modalInstance.result.then(function(result) {
						$scope.alerts.push({
							type: 'success', 
							msg: $translate("ADMIN.REPO_SUCCESFULLY_DELETED", {name: repoToDelete.value})
						});
						$scope.repositoryList.splice(indexToRemove, 1);
					});
				};
				  
				  

				$scope.getRepositoryList = function() {


				remoteRepositories.getRepositoryList().then(function(data) {
					
					// transform the repository list from object to array
					var repositoryList = [];
					for (var repoCategory in data) {
						for(var i = 0; i < data[repoCategory].length; i++) {
							var repoData = data[repoCategory][i];
							repoData.repoType = repoCategory;
							repositoryList.push(repoData);
						}
					}
					
						$scope.repositoryList = repositoryList;
					},
					function(errorMessage) {
						$scope.alerts.push({type: 'danger', msg: errorMessage});
						$scope.error;
					});
				};

				var NewCustomRepositoryForm = function($scope, $modalInstance) {
					$scope.alerts = [];
					$scope.closeAlert = function(index) {
					    $scope.alerts.splice(index, 1);
					  };
					$scope.customRepo = {
						repoType: "SOLR"
					};

					$scope.cancel = function() {
						$modalInstance.dismiss('cancel');
					};
					$scope.createRepo = function() {
						$log.info("Creating custom repository");
						$scope.disableCreateButton = true;
						$scope.alerts = [];
						$scope.savedRepo = remoteRepositories.save($scope.customRepo).then(function(data){
							if (data.status ==='SUCCESS') {
								$modalInstance.close(data.result);								
							} else {
								$scope.alerts = [];
								for (var i = 0; i < data.result.length; i++) {
									$scope.alerts.push({type:"danger", msg:$translate("ADMIN." + data.result[i].code, data.result[i])});									
								}
								$scope.disableCreateButton = false;
							}
						}, function(cause){
							$scope.alerts.push({type:"danger", msg:cause});
							$scope.disableCreateButton = false;
						});

					};
				};
				
				var ExistingRepoController = function($scope, $modalInstance) {
					$log.info("Opening existing repository dialog");
					$scope.alerts = [];
					
					$scope.cancel = function() {
						$modalInstance.dismiss('cancel');
					};
					
					$scope.existingRepo = {};
					
					predefinedRepositories.getPredefinedNotInCustom().then(function(data) {
						$scope.predefinedRepoList = data;
					}, function(reason) {
						alerts.push({type: "danger", key: reason});
					});
					
					$scope.createRepo = function () {
						$scope.disableCreateButton = false;
						var selectedRepo = $scope.existingRepo.existingRepo;
						var customRepo = { repoType: selectedRepo.serviceType, name: selectedRepo.name, repoUrl: selectedRepo.url };
								
				
						$scope.savedRepo = remoteRepositories.save(customRepo).then(function(data) {
							if (data.status ==='SUCCESS') {
								$modalInstance.close(data.result);								
							} else {
								$scope.alerts = [];
								for (var i = 0; i < data.result.length; i++) {
									$scope.alerts.push({type:"danger", msg:$translate("ADMIN." + data.result[i].code, data.result[i])});									
								}
								$scope.disableCreateButton = false;
							}
						}, function(cause){
							$scope.alerts.push({type:"danger", msg:cause});
							$scope.disableCreateButton = false;
						});
					};
					
					
					
				};

				$scope.openNewCustomRepoModal = function() {
					var modalInstance = $modal.open({
						templateUrl: 'resources/newCustomRepositoryForm.html',
						controller: NewCustomRepositoryForm,
						backdrop: 'static',
						keyboard: false
					});
					modalInstance.result.then(function(createdRepo) {
						$scope.alerts.push({
							type:'success', 
							msg: $translate("ADMIN.CUSTOM_REPO_CREATED", {name: createdRepo.name})
						});
						$scope.repositoryList.push({
							repoType: createdRepo.serviceType, 
							key: createdRepo.id, 
							value:createdRepo.name});
						});

				};
				
				$scope.openExistingRepoModal = function() {
					var modalInstance = $modal.open({
						templateUrl: 'resources/openExistingRepoModalForm.html',
						controller: ExistingRepoController,
						backdrop: 'static',
						keyboard: false
					});
					
					modalInstance.result.then(function(createdRepo){
						$scope.alerts.push({
							type: 'success', 
							msg: $translate("ADMIN.EXISTING_REPO_ADDED", createdRepo)});
						$scope.repositoryList.push({
							repoType: createdRepo.serviceType, 
							key: createdRepo.id, 
							value:createdRepo.name});
					});
				};

				$scope.getRepositoryList();

			}
		]);
})();