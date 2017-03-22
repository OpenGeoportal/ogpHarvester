(function() {
	'use strict';

	angular.module('ogpHavester.controllers.manageLayersCtrl', ['ogpHarvester.services', 'ngRoute', 'ui.bootstrap', 'pascalprecht.translate'])

	.config(['$routeProvider',
		function config($routeProvider) {
			$routeProvider.when('/manageLayers', {
				template: 'resources/layerList.html',
				controller: 'ManageLayersCtrl'
			});
		}
	])
		.controller('ManageLayersCtrl', function($scope, $http) {
			  $http.get('http://localhost:8083/datasets')
		       .then(function(res){
		          $scope.jsonresult = res.data;                
		        });
			  
			  $scope.layerDetails = function() {				  
				  ngDialog.open("http://localhost:8083/workspaces/db/datasets/cb_2015_01_bg_500k");		  
			  };
		});
})();