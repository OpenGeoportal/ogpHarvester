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

		.controller('ManageLayersCtrl', function($scope, $http, $translate, $modal) {

            $scope.itemsByPage = 12;
            $scope.jsonresult = [];
            $scope.displayedCollection = [].concat($scope.jsonresult);
            $scope.actions = [$translate("MANAGE_LAYERS.DOWNLOAD"), $translate("MANAGE_LAYERS.UPDATE"),
                $translate("MANAGE_LAYERS.DELETE")];

            $scope.GetValue = function (action, row_name) {
                //console.log(action + " " + row_name);
                var res = row_name.split(":");
                $scope.ws=res[0];
                $scope.ds= res[1];
                switch(action) {
                    case $translate("MANAGE_LAYERS.DOWNLOAD"):
                        console.log("download " + "http://localhost:8083/workspaces/"+
                            $scope.ws + "/datasets/" + $scope.ds + "/download");

                    case $translate("MANAGE_LAYERS.UPDATE"):

                    case $translate("MANAGE_LAYERS.DELETE"):

                }




            };

            $http({
                method : "GET",
                //url : "http://localhost:8083/allDatasets",
                url : "http://localhost:8083/workspaces/db/datasets?pageSize=200",
                isArray: true
            }).then(function mySuccess(response) {
                $scope.jsonresult = response.data;
                //$scope.dispPages = Math.round(response.data.totalElements/$scope.itemsByPage + 0.5);
            }, function myError(response) {
                $scope.jsonresult = response.statusText;
            });

            $scope.layerDetails = function (row_name) {
                var res = row_name.split(":");
                $scope.ws=res[0];
                $scope.ds= res[1];
                var splash = $modal.open({
                    animation: true,
                    templateUrl: 'resources/splash.html',
                    keyboard: false,
                    backdrop: 'static',
                    scope: $scope,
                });
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
                                splash.close();
                            }).error(function(response){
                                splash.close();
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