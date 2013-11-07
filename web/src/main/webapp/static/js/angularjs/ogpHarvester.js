/**
 *
 */
'use strict';

angular.module(
	'ogpHarvester', ['ngRoute', 'pascalprecht.translate', 'ngResource',
		'ogpHarvester.filters', 'ogpHarvester.services',
		'ogpHarvester.services', 'ogpHarvester.directives',
		'ogpHarvester.controllers'
	]).config(
	['$routeProvider',
		function($routeProvider) {
			$routeProvider.when('/manageIngests', {
				templateUrl: '../resources/ingestsList.html',
				controller: 'ManageIngestsCtrl'
			});
			$routeProvider.when('/manageIngests/view/:id', {
				templateUrl: '../resources/ingestDetails.html',
				controller: 'IngestDetailsCtrl'

			})
			$routeProvider.otherwise({
				redirectTo: '/manageIngests'
			});
		}
	]).config(['$translateProvider',
	function($translateProvider) {
		$translateProvider.useStaticFilesLoader({
			prefix: '../resources/locales/locale-',
			suffix: '.json'
		});
		$translateProvider.preferredLanguage('en');
	}
]);