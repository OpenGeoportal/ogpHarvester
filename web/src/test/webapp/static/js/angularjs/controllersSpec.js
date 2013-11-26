describe("Test controllers", function() {
	var $scope;
	// mock ogpHarvester to alow us to inject our own dependencies
	beforeEach(module('ogpHarvester.controllers'));
	beforeEach(module('ngRoute'));
	beforeEach(module('ogpHarvester.services'));
	describe('MenuCtrl', function() {
		var $location;
		// mock the controller for the same reason and include $rootScope and
		// $controller
		beforeEach(inject(function($rootScope, $controller, _$location_) {
			$location = _$location_;
			$scope = $rootScope.$new();
			// declare the controller and inject our empty scope
			$controller('MenuCtrl', {
				$scope : $scope
			});
		}));

		// tests start here

		it("should define getClass function", function() {
			expect($scope.getClass).not.toBeUndefined();
		}); 
		it("should return active when location starts with linkPath", function () {
			$location.path('/myPath/otherSegment/anotherOne');
			$scope.$eval();
			var active = $scope.getClass('/myPath');
			expect(active).toBe('active');
			active = $scope.getClass('/myPath/otherSegment');
			expect(active).toBe('active');
			active = $scope.getClass('/otherSegment');
			expect(active).toBe('');
		});
		
	});

});