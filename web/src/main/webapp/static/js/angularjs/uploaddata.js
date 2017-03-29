(function() {
	'use strict';

	angular.module('ogpHavester.controllers.uploadDataCtrl', ['ogpHarvester.services', 'ngRoute', 'ui.bootstrap', 'pascalprecht.translate', 'ngFileUpload'])

	.config(['$routeProvider',
		function config($routeProvider) {
			$routeProvider.when('/uploadData', {
				template: 'resources/uploaddata.html',
				controller: 'UploadDataCtrl'
			});
		}
	]).controller('UploadDataCtrl', ['$scope', 'Upload', '$http', function ($scope, Upload, $http)  {
			 
			$scope.downloads = [];
	
			$scope.uploadFiles = function(file) {
		        if (file) {
		        	file.upload = Upload.upload({
		                url: 'http://localhost:8080',
		                data: {workspace: $scope.workspace, dataset: $scope.dataset, zipFile:  $scope.zipFile}
		            });

		        	file.upload.then(function (response) {
		            	$scope.downloads.push({'workspace': $scope.workspace, 'dataset': $scope.dataset, 'fileName':  $scope.zipFile.name, 'fileSize':  $scope.zipFile.size, 'done':false})
		            }, function (response) {
		                if (response.status > 0)
		                    $scope.errorMsg = response.status + ': ' + response.data;
		            });
		        }   
		    }
		}]);

})();