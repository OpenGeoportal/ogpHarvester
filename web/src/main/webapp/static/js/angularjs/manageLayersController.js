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
		.controller('ManageLayersCtrl', ['$scope', 'remoteRepositories', 'predefinedRepositories',
			'$modal', '$log', '$translate',

			function ManageIngestsCtrl($scope, remoteRepositories, predefinedRepositories, $modal, $log, $translate) {


			}
		]);
})();