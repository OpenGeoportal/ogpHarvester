(function() {
	'use strict';

	angular.module('ogpHavester.controllers.manageLayersCtrl',
        ['ogpHarvester.services', 'ngRoute', 'ui.bootstrap', 'pascalprecht.translate', 'smart-table'])

	.config(['$routeProvider',
		function config($routeProvider) {
			$routeProvider.when('/manageLayers', {
				template: 'resources/layerList.html',
				controller: 'ManageLayersCtrl'
			});
		}
	])
/*
    .controller('ManageLayersCtrl', ['$scope', function (scope) {
        scope.rowCollection = [
            {firstName: 'Laurent', lastName: 'Renard', birthDate: new Date('1987-05-21'), balance: 102, email: 'whatever@gmail.com'},
            {firstName: 'Blandine', lastName: 'Faivre', birthDate: new Date('1987-04-25'), balance: -2323.22, email: 'oufblandou@gmail.com'},
            {firstName: 'Francoise', lastName: 'Frere', birthDate: new Date('1955-08-27'), balance: 42343, email: 'raymondef@gmail.com'}
        ];


    }]);
*/


		.controller('ManageLayersCtrl', function($scope, $http) {

            $scope.itemsByPage=5;

            $http({
                method : "GET",
                url : "http://localhost:8083/workspaces/sf/datasets",
                isArray: true
            }).then(function mySucces(response) {
                $scope.jsonresult = response.data;
            }, function myError(response) {
                $scope.jsonresult = response.statusText;
            });


/*
			  $scope.layerDetails = function() {
				  ngDialog.open("http://localhost:8083/workspaces/db/datasets/cb_2015_01_bg_500k");
			  };
*/




		});
})();