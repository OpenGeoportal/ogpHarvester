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
                var res = row_name.split(":");
                $scope.ws=res[0];
                $scope.ds= res[1];
                var modalInstance = $modal.open({
                    templateUrl: 'resources/popup.html',
                    controller: 'PopupCtrl',
                    resolve: {
                        jsonresp:function(){
                            return $http({
                                method : "GET",
                                url : "http://localhost:8083/workspaces/" + $scope.ws + "/datasets/" + $scope.ds,
                                isArray: true
                            }).success(function (response) {
                            }).error(function(response){
                                $scope.details = response.statusText;
                            });
                        }
                    }
                });
            }



		})

    .controller('PopupCtrl', ['$scope','$modalInstance', 'jsonresp', function ($scope, $modalInstance, jsonresp) {
        $scope.details = jsonresp.data;
        $scope.layerTitle=jsonresp.data.title;

        $scope.close = function () {
            $modalInstance.dismiss('close');
        };

    }]);




})();