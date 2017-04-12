(function() {
	'use strict';

	angular.module('ogpHavester.controllers.uploadDataCtrl', ['ogpHarvester.services', 'ngRoute', 'ui.bootstrap', 'pascalprecht.translate', 'ngFileUpload', 'ngCookies'])

	.config(['$routeProvider',
		function config($routeProvider) {
		$routeProvider.when('/uploadData', {
			template: 'resources/uploaddata.html',
			controller: 'UploadDataCtrl'
		});
	}
	]).controller('UploadDataCtrl', ['$scope', 'Upload', '$http', '$q','$cookies', '$interval', '$translate', 'defaultWorkspaces', '$modal', function ($scope, Upload, $http, $q, $cookies, $interval, $translate, defaultWorkspaces, $modal)  {

		try { angular.module("ngFileUpload") } catch(err) { console.log(err); }
		try { angular.module("ngCookies") } catch(err) { console.log(err); }

		if($cookies['downloads']!=null) {
			$scope.downloads = JSON.parse($cookies['downloads']);
		} else {
			$scope.downloads = [];
			$cookies['downloads'] = JSON.stringify($scope.downloads);
		}

		$scope.refreshView = $interval(function(){
			angular.forEach($scope.downloads, function(download) {	
				if(download.locked && download.ticket>=0) {
					$http({
		                method : "GET",
		                url : "http://localhost:8083/workspaces/topp/datasets"
		            }).then(function mySucces(response) {
		                if(response.status=='200') {
			                download.status = $translate("UPLOAD_DATA.FILE_SENT");
							download.statusColor = 'black';
							download.zipFile = '';
							download.ticket=-1;
		                } else {
		                	download.status = response.data;
		                }
		            }, function myError(response) {
		            	download.status = response.data;
		            });
				}				
			});
		},1000);

		$scope.uploadFiles = function(files, errFiles) {
			$scope.files = files;
			$scope.errFiles = errFiles;
			angular.forEach(files, function(file) {
				if(file.name.substr(file.name.length - 4, file.name.length)==='.zip') {
					$scope.downloads.push({'workspace': '', 'dataset': file.name.substr(0, file.name.length - 4), 'fileName':  file.name, 'fileSize':  $scope.bytesConverter(file.size, 2), 'zipFile': file, 'status' : $translate("UPLOAD_DATA.READY"), 'statusColor' : 'green', 'valid' : true, 'locked' : false, 'ticket' : 0})
				} else {
					$scope.downloads.push({'workspace': '', 'dataset': file.name.substr(0, file.name.length - 4), 'fileName':  file.name, 'fileSize':  $scope.bytesConverter(file.size, 2), 'zipFile': file, 'status' : $translate("UPLOAD_DATA.NOT_A_ZIP_FILE"), 'statusColor' : 'red', 'valid' : false, 'locked' : false, 'ticket' : 0})
				}
			});
			$cookies['downloads'] = JSON.stringify($scope.downloads);
		}

		$scope.sendFiles = function() {

			var validSet = true;

			angular.forEach($scope.downloads, function(download) {
				if(!download.locked && download.valid && download.workspace==='') {
					download.status = $translate("UPLOAD_DATA.WORKSPACE_NAME_REQUIRED");
					download.statusColor = 'red';
					validSet = false
				} else if(!download.locked && download.valid && download.dataset==='') {
					download.status = $translate("UPLOAD_DATA.DATASET_NAME_REQUIRED");
					download.statusColor = 'red';
					validSet = false
				} else if(!download.locked && !download.valid) {
					download.status = $translate("UPLOAD_DATA.NOT_ALLOWED");
					download.statusColor = 'red';
					validSet = false
				} else if(!download.locked) {
					download.status = $translate("UPLOAD_DATA.READY");
					download.statusColor = 'green';
				}
			});			

			if(validSet) {
				angular.forEach($scope.downloads, function(download) {
					if(download.valid && !download.locked) {
						
						download.status = $translate("UPLOAD_DATA.UPLOADING");
						download.statusColor = 'blue';

						Upload.upload({
							url: 'http://localhost:8083/workspaces/'+download.workspace+'/datasets/'+download.dataset,
							data: {file: download.zipFile}
						}).then(function (resp) {
							console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
							download.status = $translate("UPLOAD_DATA.SENDING");
							download.ticket = resp.data.split('*')[0];
							download.statusColor = 'blue';
							download.locked = true;
						}, function (resp) {
							console.log('Error status: ' + resp.status);
							if(resp.status=='500') {
								download.status = resp.data.message;
								download.statusColor = 'red';
								download.locked = false;
							} else {
								download.status = resp.data;
								download.statusColor = 'red';
								download.locked = false;
							}
						});

					}
				});
			}

			$cookies['downloads'] = JSON.stringify($scope.downloads);
		}

		$scope.expandWorkspace = function(workspace) {
			if(workspace!=null && workspace!='') {
				if ($scope.downloads.length>1 && window.confirm($translate("UPLOAD_DATA.RENAME_ALL_WORKSPACES_WITH", {
					name : workspace
				})) ) {
					angular.forEach($scope.downloads, function(download) {
						if(!download.locked) {
							download.workspace = workspace;
						}
					});
					$cookies['downloads'] = JSON.stringify($scope.downloads);
				}
			}
		}

		$scope.remove = function(array, index){
			if(window.confirm($translate("UPLOAD_DATA.REMOVE_THE_FILE"))) {
				array.splice(index, 1);
				$cookies['downloads'] = JSON.stringify(array);
			}
		}

		$scope.clean = function(array, index){
			array.splice(index, 1);
			$cookies['downloads'] = JSON.stringify(array);
		}

		$scope.bytesConverter = function(bytes, precision) {
			if (isNaN(parseFloat(bytes)) || !isFinite(bytes)) return '-';
			if (typeof precision === 'undefined') precision = 1;
			var units = ['bytes', 'kB', 'MB', 'GB', 'TB', 'PB'],
			number = Math.floor(Math.log(bytes) / Math.log(1024));
			return (bytes / Math.pow(1024, Math.floor(number))).toFixed(precision) +  ' ' + units[number];
		};		

		$scope.getDefaultWorkspaces = function() {


			defaultWorkspaces.getWorkspaces().then(function(response) {

				$scope.workspaceList = response.data;
			},
			function(errorMessage) {
				$scope.alerts.push({
					type: 'danger',
					msg: errorMessage
				});
				$scope.error;
			});
		};

		$scope.getDefaultWorkspaces();	
	}]);

})();