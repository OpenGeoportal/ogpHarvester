(function() {
    'use strict';
    
    var interceptorsModule = angular.module('ogpHavester.interceptors', []);
    
    interceptorsModule.factory('sessionExpiredInterceptor',['$q', '$window', 
        function($q, $window) {
            return function(promise) {
                return promise.then(
                        function(response) {
                            return response;
                        }, function (response) {
                            if (response.status === 440) {
                                $window.location.reload();
                                return $q.reject(response);
                            } else {
                                return $q.reject(response);
                            }                           
                        });
                    };
                }
            ]);
    interceptorsModule.config(function($provide, $httpProvider) {
        $httpProvider.responseInterceptors.push("sessionExpiredInterceptor");
	// Adding header to identify AJAX request
	$httpProvider.defaults.headers.common["X-ats-type"] = "ajax";
    });
    
    
})();
