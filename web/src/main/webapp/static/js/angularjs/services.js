'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('ogpHarvester.services', ['ngResource'])
	.factory('Ingests', function($resource) {
		return $resource('../ingests', {});
	})
	.value('version', '0.1');