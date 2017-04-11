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

		.controller('ManageLayersCtrl', function($scope, $http, $modal) {

            $scope.itemsByPage = 12;
            $scope.jsonresult = [];
            $scope.displayedCollection = [].concat($scope.jsonresult);


            $http({
                method : "GET",
                //url : "http://localhost:8083/allDatasets",
                url : "http://localhost:8083/workspaces/topp/datasets",
                isArray: true
            }).then(function mySucces(response) {
                $scope.jsonresult = response.data;
                //$scope.dispPages = Math.round(response.data.totalElements/$scope.itemsByPage + 0.5);
            }, function myError(response) {
                $scope.jsonresult = response.statusText;
            });


            $scope.layerDetails = function (row_name) {
                    //console.log("the name of this layers is: " + row_name);
                var modalInstance = $modal.open({
                    templateUrl: 'resources/popup.html',
                    controller: 'PopupCtrl',
                    resolve: {
                        titlename2: function () {
                            return row_name;
                        }
                    }
                });
            }



		})

    .controller('PopupCtrl', ['$scope','$modalInstance', 'titlename2', function ($scope, $modalInstance, titlename2) {
        $scope.title1 = titlename2;
        /*
        $scope.close = function () {
            $modalInstance.dismiss('cancel');
        };*/

    }]);




})();