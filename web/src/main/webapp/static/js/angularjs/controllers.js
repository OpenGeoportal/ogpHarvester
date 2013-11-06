'use strict';

/* Controllers */

angular.module('ogpHarvester.controllers', []);
angular.module("ogpHarvester.controllers")
	.controller('ManageIngestsCtrl', ['$scope', 'Ingests',
		function($scope, Ingests) {
			$scope.data = {};
			Ingests.query(function(response) {
				$scope.data.ingests = response;
			});
		}
	]);
angular.module('ogpHarvester.controllers')
	.controller('IngestDetailsCtrl', ['$scope', '$routeParams',
		function($scope, $routeParams) {
			$scope.params = $routeParams;


		}
	]);