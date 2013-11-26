describe("Test controllers", function() {
	var scope;
	// mock ogpHarvester to alow us to inject our own dependencies
	beforeEach(module('ogpHarvester.controllers'));
	beforeEach(module('ngRoute'));
	beforeEach(module('ogpHarvester.services'));
	describe('MenuCtrl', function() {
		// mock the controller for the same reason and include $rootScope and
		// $controller
		beforeEach(inject(function($rootScope, $controller) {
			scope = $rootScope.$new();
			// declare the controller and inject our empty scope
			$controller('MenuCtrl', {
				$scope : scope
			});
		}));

		// tests start here

		it("should define getClass function", function() {
			expect(scope.getClass).not.toBeUndefined();
		});

		
		
	});

});