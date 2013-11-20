/**
 *
 */
'use strict';

(function () {
	angular.module(
		'ogpHarvester', ['ngRoute', 'pascalprecht.translate', 'ngResource', 'ngAnimate', 'rcForm', 'ui.jq', 'template/tooltip/tooltip-popup.html', 'ui.bootstrap.tooltip',
			'ogpHarvester.filters', 'ogpHarvester.services',
			'ogpHarvester.services', 'ogpHarvester.directives',
			'ogpHarvester.controllers'
		]).config(
		['$routeProvider', '$locationProvider',
			function ($routeProvider, $locationProvider) {
				//$locationProvider.html5Mode(true);
				$routeProvider.when('/manageIngests', {
					templateUrl: 'resources/ingestsList.html',
					controller: 'ManageIngestsCtrl'
				});
				$routeProvider.when('/manageIngests/view/:id', {
					templateUrl: 'resources/ingestDetails.html',
					controller: 'IngestDetailsCtrl'

				});
				$routeProvider.when('/newIngest', {
					templateUrl: 'resources/newIngestForm.html',
					controller: 'NewIngestCtrl'
				});
				$routeProvider.when('/newIngest/step2', {
					templateUrl: 'resources/newIngestFormStep2.html',
					controller: 'NewIngestCtrl'
				});

				$routeProvider.otherwise({
					redirectTo: '/manageIngests'
				});
			}
		]).config(['$translateProvider',
		function ($translateProvider) {
			$translateProvider.useStaticFilesLoader({
				prefix: 'resources/locales/locale-',
				suffix: '.json'
			});
			$translateProvider.preferredLanguage('en');
		}
	]);
	angular.module('ogpHarvester').value('uiJqConfig', {
		datetimepicker: {
			pickTime: false
		}
	});
})();