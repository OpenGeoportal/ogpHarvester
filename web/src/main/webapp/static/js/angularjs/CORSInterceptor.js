(function() {
    'use strict';

  var module = angular.module('ogpHavester.interceptors.cors', []);

  /**
   * CORS Interceptor
   *
   * This interceptor checks if each AJAX call made in AngularJS needs a proxy
   * or not.
   */

  module.config([
    '$httpProvider',
    function($httpProvider) {
      $httpProvider.interceptors.push([
        '$q',
        '$injector',
        'gnGlobalSettings',
        'gnLangs',
        function($q, $injector, gnGlobalSettings, gnLangs) {
          return {
            request: function(config) {
              if (gnLangs.current) {
                config.headers['Accept-Language'] = gnLangs.current;
              }
              if (config.url.indexOf('http', 0) === 0) {
                var url = config.url.split('/');
                url = url[0] + '/' + url[1] + '/' + url[2] + '/';

                if ($.inArray(url, gnGlobalSettings.requireProxy) != -1) {
                  // require proxy                	
                  config.url = gnGlobalSettings.proxyUrl +
                      encodeURIComponent(config.url);
                  console.log(config.url);
                }
              }

              return $q.when(config);
            },
            responseError: function(response) {
              var config = response.config;

              if (config.nointercept) {
                return $q.when(config);
              // let it pass
              } else if (!config.status || config.status == -1) {
                var defer = $q.defer();

                if (config.url.indexOf('http', 0) === 0) {
                  var url = config.url.split('/');
                  url = url[0] + '/' + url[1] + '/' + url[2] + '/';

                  if ($.inArray(url, gnGlobalSettings.requireProxy) == -1) {
                    gnGlobalSettings.requireProxy.push(url);
                  }

                  $injector.invoke(['$http', function($http) {
                    // This modification prevents interception (infinite
                    // loop):

                    config.nointercept = true;

                    // retry again
                    $http(config).then(function(resp) {
                      defer.resolve(resp);
                    }, function(resp) {
                      defer.reject(resp);
                    });
                  }]);

                } else {
                  return $q.reject(response);
                }

                return defer.promise;
              } else {
                return response;
              }
            }
          };

        }
      ]);
    }
  ]);

})();