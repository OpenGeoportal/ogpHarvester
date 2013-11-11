'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('ogpHarvester.services', ['ngResource'])
	.factory('Ingest', function($resource) {
		return $resource('../rest/ingests/:id', {}, {
			'query': {
				method: 'GET',
				url: '../rest/ingests',
				isArray: true
			}

		});
	})
	.value('version', '0.1');