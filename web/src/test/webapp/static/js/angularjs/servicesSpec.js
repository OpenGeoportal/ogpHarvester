describe('Test services', function() {
	var remoteRepositories, $httpBackend, repoList;
	beforeEach(module('ogpHarvester.services'));

	describe('remoteRepositories', function() {
		beforeEach(inject(function(_remoteRepositories_, _$httpBackend_) {
			remoteRepositories = _remoteRepositories_;
			$httpBackend = _$httpBackend_;
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

		}));
		afterEach(function() {
			$httpBackend.flush();
			$httpBackend.verifyNoOutstandingExpectation();
			$httpBackend.verifyNoOutstandingRequest();
		});

		it('should get the repositories list', function() {
			$httpBackend.when('GET', 'rest/repositories').respond(repoList);
			remoteRepositories.getRepositoryList().then(function(data) {
				expect(data).toEqual(repoList);
			}, function(errorText) {
				//should not error with $httpBackend interceptor 200 status
				expect(false).toEqual(true);
			});

		});

		it('should return error text if server respond error', function() {
			$httpBackend.when('GET', 'rest/repositories').respond(500, '');
			remoteRepositories.getRepositoryList().then(function(data) {
					expect("never called when error").toEqual(false);
				},
				function(errorText) {
					expect(errorText).toBeDefined();
				})
		});
	});

});