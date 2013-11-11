'use strict';

/* Directives */

angular.module('ogpHarvester.directives', []).directive('appVersion',
		[ 'version', function(version) {
			return function(scope, elm, attrs) {
				elm.text(version);
			};
		} ]);
