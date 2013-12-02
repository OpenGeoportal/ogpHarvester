(function() {
	'use strict';

	angular.module('ogpHavester.controllers.adminCtrl', ['ogpHarvester.services', 'ngRoute', 'ui.bootstrap'])

	.config(['$routeProvider',
		function config($routeProvider) {
			$routeProvider.when('/admin', {
				template: 'resources/admin.html',
				controller: 'AdminCtrl'
			});
		}
	])
		.controller('AdminCtrl', ['$scope', 'remoteRepositories', 'predefinedRepositories',
		                          '$modal', '$log',

			function AdminCtrl($scope, remoteRepositories, predefinedRepositories, $modal, $log) {
			
				$scope.alerts = [];
				$scope.closeAlert = function(index) {
				    $scope.alerts.splice(index, 1);
				};
				

				//  Remove modal dialog controller
				var DeleteRepositoryCtrl  = function($scope, $modalInstance, repoToDelete) {
					$scope.repoToDelete = repoToDelete;
					$scope.alerts = [];
					$scope.closeAlert = function(index) {
					    $scope.alerts.splice(index, 1);
					};
					
					$scope.cancel = function() {
						$modalInstance.dismiss('cancel');
					};
					
					$scope.deleteRepo = function() {
						var id = $scope.repoToDelete.key;
						remoteRepositories.remove(id).then(function() {
							$modalInstance.close();
						},
						function(reason) {
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
						controller: DeleteRepositoryCtrl,
						resolve: {
							repoToDelete: function() {
								return repoToDelete;
							}
						}
					});
					
					modalInstance.result.then(function(result) {
						$scope.alerts.push({type: 'success', msg: repoToDelete.value + " has been successfully removed"});
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
						$scope.savedRepo = remoteRepositories.save($scope.customRepo).then(function(data){
							$modalInstance.close(data);
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
						var selectedRepo = $scope.existingRepo.existingRepo;
						var customRepo = { repoType: selectedRepo.serviceType, name: selectedRepo.name, repoUrl: selectedRepo.url };
								
				
						$scope.savedRepo = remoteRepositories.save(customRepo).then(function(data){
							$modalInstance.close(data);
						}, function(cause){
							$scope.alerts.push({type:"danger", msg:cause});
							$scope.disableCreateButton = false;
						});
					};
					
					
					
				};

				$scope.openNewCustomRepoModal = function() {
					var modalInstance = $modal.open({
						templateUrl: 'resources/newCustomRepositoryForm.html',
						controller: NewCustomRepositoryForm
					});
					modalInstance.result.then(function(createdRepo) {
						$scope.alerts.push({type:'success', msg: 'The repository has been successfully created'});
						$scope.repositoryList.push({
							repoType: createdRepo.serviceType, 
							key: createdRepo.id, 
							value:createdRepo.name});
						});

				};
				
				$scope.openExistingRepoModal = function() {
					var modalInstance = $modal.open({
						templateUrl: 'resources/openExistingRepoModalForm.html',
						controller: ExistingRepoController
					});
					
					modalInstance.result.then(function(createdRepo){
						$scope.alerts.push({type: 'success', msg: 'The repository has been successfully added to My Repositories'});
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