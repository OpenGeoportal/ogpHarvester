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
		            	$scope.downloads.push({'workspace': $scope.workspace, 'dataset': $scope.dataset, 'fileName':  $scope.zipFile.name, 'fileSize':  $scope.bytesConverter($scope.zipFile.size, 2), 'done':false})
		            }, function (response) {
		                if (response.status > 0)
		                    $scope.errorMsg = response.status + ': ' + response.data;
		            });
		        }   
		    }
			
			$scope.bytesConverter = function(bytes, precision) {
					if (isNaN(parseFloat(bytes)) || !isFinite(bytes)) return '-';
					if (typeof precision === 'undefined') precision = 1;
					var units = ['bytes', 'kB', 'MB', 'GB', 'TB', 'PB'],
						number = Math.floor(Math.log(bytes) / Math.log(1024));
					return (bytes / Math.pow(1024, Math.floor(number))).toFixed(precision) +  ' ' + units[number];
			};
			
			
		}]);

})();