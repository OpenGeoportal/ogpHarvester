describe("Test admin controller", function() {
	var remoteRepositories;
	var $scope;
	var repoList, flatRepoList;

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
		
		flatRepoList =  [{
			"key": 18,
			"repoType": "WEBDAV",
			"value": "Remote WebDAV instance 1"
		}, {
			"key": 19,
			"repoType": "WEBDAV",
			"value": "Remote WebDAV instance 2"
		},{
			"key": 1,
			"repoType": "SOLR",
			"value": "Remote Solr instance 1"
		}, {
			"key": 2,
			"repoType": "SOLR",
			"value": "Remote Solr instance 2"
		},{
			"key": 7,
			"repoType": "GEONETWORK",
			"value": "Remote Geonetwork instance 1"
		}, {
			"key": 8,
			"repoType": "GEONETWORK",
			"value": "Remote Geonetwork instance 2"
		}, {
			"key": 12,
			"repoType": "CSW",
			"value": "Remote CSW instance 1"
		}, {
			"key": 13,
			"repoType": "CSW",
			"value": "Remote CSW instance 2"
		}];
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
		expect($scope.repositoryList).toEqual(flatRepoList);
		expect(remoteRepositories.getRepositoryList).toHaveBeenCalled();
	});
	
	it('should define alerts array', function() {
		expect($scope.alerts).toBeDefined();
	});
	
	it('should remove an array element from alerts', function() {
		$scope.alerts = [{type:'success', msg:'msg1'}, {type:'success', msg:'msg2'}, {type:'success', msg:'msg3'}];
		$scope.closeAlert(1);
		expect($scope.alerts).not.toContain({type:'success', msg:'msg2'});
		expect($scope.alerts.length).toBe(2);
	});
});