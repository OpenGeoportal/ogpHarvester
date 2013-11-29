describe("Test admin controller", function() {
	var remoteRepositories;
	var $scope;
	var repoList;

	beforeEach(module('ogpHavester.controllers.adminCtrl'));
	beforeEach(function() {
		repoList = {
			"WEBDAV": [{
				"key": 18,
				"value": "Remote WebDAV instance 1"
			}, {
				"key": 19,
				"value": "Remote WebDAV instance 2"
			}],
			"SOLR": [{
				"key": 1,
				"value": "Remote Solr instance 1"
			}, {
				"key": 2,
				"value": "Remote Solr instance 2"
			}],
			"GEONETWORK": [{
				"key": 7,
				"value": "Remote Geonetwork instance 1"
			}, {
				"key": 8,
				"value": "Remote Geonetwork instance 2"
			}],
			"CSW": [{
				"key": 12,
				"value": "Remote CSW instance 1"
			}, {
				"key": 13,
				"value": "Remote CSW instance 2"
			}]
		};
		// mock remoteRepositories service
		remoteRepositories = {
			getRepositoryList: function() {
				return {
					then: function(callback) {
						callback(repoList);
					}
				};
			}
		};
	});

	beforeEach(
		inject(function(_$rootScope_, $controller) {
		$scope = _$rootScope_.$new();
		$controller('AdminCtrl', {
			$scope: $scope,
			remoteRepositories: remoteRepositories
		});
		
	}));

	it('should call getRepositoryList remoteRepositories service method', function() {
		spyOn(remoteRepositories, 'getRepositoryList').andCallThrough();
		$scope.getRepositoryList();
		expect($scope.repositoryList).toBeDefined();
		expect($scope.repositoryList).toEqual(repoList);
		expect(remoteRepositories.getRepositoryList).toHaveBeenCalled();
	});

});