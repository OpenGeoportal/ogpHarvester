(function() {
	'use strict';

	angular.module('ogpHavester.controllers.manageLayersCtrl',
        ['ogpHarvester.services', 'ngRoute', 'ui.bootstrap', 'pascalprecht.translate', 'smart-table'])

	.config(['$routeProvider',
		function config($routeProvider) {
			$routeProvider.when('/manageLayers', {
				template: 'resources/layerList.html',
				controller: 'ManageLayersCtrl'
			});
		}
	])

        .controller('ManageLayersCtrl', ['$scope', '$http', '$translate', '$modal', '$route', '$window', '__env',
            function ($scope, $http, $translate, $modal, $route, $window, __env) {

        	var dataIngestURL = __env.dataIngestAPIUrl;
            $scope.itemsByPage = 12;
            $scope.jsonresult = [];
            $scope.displayedCollection = [].concat($scope.jsonresult);
            $scope.actions = [$translate("MANAGE_LAYERS.DOWNLOAD"),
                $translate("MANAGE_LAYERS.DELETE")];
            
            $scope.token = '';
    		// Initialize the token for JWT authentication with DataIngest				
    		$http({
    			method : "GET",
    			url : 'getDataIngestToken'
    		}).then(function mySucces(response) {
    			$scope.token = response.data;												
    		}, function myError(response) {
    			console.log('Invalid session');
    		});

            $scope.GetValue = function (action, row_name) {
                 var res = row_name.split(":");
                $scope.ws=res[0];
                $scope.ds= res[1];
                switch(action) {
                    case $translate("MANAGE_LAYERS.DOWNLOAD"):
                        var newWindow = $modal.open({
                            templateUrl: 'resources/splash.html',
                            scope: $scope,
                            controller: 'downloadWindowCtrl',
                            resolve: {
                                url: function () {
                                    return dataIngestURL + "/workspaces/" +
                                        $scope.ws + "/datasets/" + $scope.ds + "/download";
                                },
                                layer_title: function (){
                                    return $scope.ws + ":" + $scope.ds;
                                },
                                msg: function () {
                                    return $translate('SPLASH.DOWNLOAD');
                                }
                            },
                        });

                        break;
                     case $translate("MANAGE_LAYERS.DELETE"):
                        confirmDlg();
                        }
                    this.ddlActions = '';
            }
            ;

            $http({
                method : "GET",
                url : "http://localhost:8083/allDatasetsMockup",//TODO: remove this from production
                //url : "http://localhost:8083/allDatasets",
                isArray: true
            }).then(function mySuccess(response) {
                $scope.jsonresult = response.data;
            }, function myError(response) {
                $scope.jsonresult = response.statusText;
            });

            $scope.layerDetails = function (row_name) {
                var res = row_name.split(":");
                $scope.ws=res[0];
                $scope.ds= res[1];
                var splash = $modal.open({
                    animation: true,
                    templateUrl: 'resources/splash.html',
                    keyboard: false,
                    backdrop: 'static',
                    scope: $scope,
                    controller: 'SplashCtrl',
                    resolve: {
                        msg: function () {
                            return $translate('SPLASH.DETAILS');
                        },
                        layer_title: function (){
                            return $scope.ws + ":" + $scope.ds;
                        }
                    },
                });
                    var modalInstance = $modal.open({
                    templateUrl: 'resources/popup.html',
                    controller: 'PopupCtrl',
                    resolve: {
                        jsonresp:function(){
                            return $http({
                                method : "GET",
                                url : dataIngestURL + "/workspaces/" + $scope.ws + "/datasets/" + $scope.ds,
                                isArray: true
                            }).success(function (response) {
                                splash.close();
                            }).error(function(response){
                                splash.close();
                                $scope.details = response.statusText;
                            });
                        }
                    }
                });
            }





            function confirmDlg (options) {
                var deferredObject = $.Deferred();
                var defaults = {
                    type: "confirm", //alert, prompt,confirm
                    modalSize: 'modal-sm', //modal-sm, modal-lg
                    okButtonText: 'Ok',
                    cancelButtonText: 'Cancel',
                    yesButtonText: 'Yes',
                    noButtonText: 'No',
                    headerText: "You are about to remove '" + $scope.ws + ":" + $scope.ds + "'",
                    messageText: 'Are you sure you want to remove this dataset?',
                    alertType: 'danger', //default, primary, success, info, warning, danger
                }
                $.extend(defaults, options);

                var _show = function(){
                    var headClass = "alert-danger";
                    $('BODY').append(
                        '<div id="ezAlerts" class="modal fade">' +
                        '<div class="modal-dialog" class="' + defaults.modalSize + '">' +
                        '<div class="modal-content">' +
                        '<div id="ezAlerts-header" class="modal-header ' + headClass + '">' +
                        '<button id="close-button" type="button" class="close" data-dismiss="modal"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>' +
                        '<h4 id="ezAlerts-title" class="modal-title">Modal title</h4>' +
                        '</div>' +
                        '<div id="ezAlerts-body" class="modal-body">' +
                        '<div id="ezAlerts-message" ></div>' +
                        '</div>' +
                        '<div id="ezAlerts-footer" class="modal-footer">' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '</div>'
                    );

                    $('.modal-header').css({
                        'padding': '15px 15px',
                        '-webkit-border-top-left-radius': '5px',
                        '-webkit-border-top-right-radius': '5px',
                        '-moz-border-radius-topleft': '5px',
                        '-moz-border-radius-topright': '5px',
                        'border-top-left-radius': '5px',
                        'border-top-right-radius': '5px'
                    });

                    $('#ezAlerts-title').text(defaults.headerText);
                    $('#ezAlerts-message').html(defaults.messageText);

                    var keyb = "false", backd = "static";
                    var calbackParam = "";

                    var btnhtml = '<button id="ezok-btn" class="btn btn-primary">' + defaults.noButtonText + '</button>';

                    if (defaults.yesButtonText && defaults.yesButtonText.length > 0) {
                        btnhtml += '<button id="ezclose-btn" class="btn btn-default">' + defaults.yesButtonText + '</button>';
                    }
                    $('#ezAlerts-footer').html(btnhtml).on('click', 'button', function (e) {
                        if (e.target.id === 'ezok-btn') {//false
                            calbackParam = false;
                            //console.log("cancel");
                            $('#ezAlerts').modal('hide');
                        } else if (e.target.id === 'ezclose-btn') {//true
                            calbackParam = true;
                            $('#ezAlerts').modal('hide');
                        }
                    });

                    $('#ezAlerts').modal({
                        show: false,
                        backdrop: backd,
                        keyboard: keyb
                    }).on('hidden.bs.modal', function (e) {
                        $('#ezAlerts').remove();
                        deferredObject.resolve(calbackParam);
                        deferredObject.then(function(calbackParam){
                            if(calbackParam) {
                                var message = "helloworld";
                                var splash = $modal.open({
                                    animation: true,
                                    templateUrl: 'resources/splash.html',
                                    keyboard: false,
                                    backdrop: 'static',
                                    scope: $scope,
                                    controller: 'SplashCtrl',
                                    resolve: {
                                        msg: function () {
                                            return $translate('SPLASH.DELETE');
                                        },
                                        layer_title: function (){
                                            return $scope.ws + ":" + $scope.ds;
                                        }
                                    },

                                });
                                $http({
                                    method : "DELETE",
                                    url : dataIngestURL + "/workspaces/" + $scope.ws + "/datasets/" + $scope.ds,
                                    headers: {'Authorization': $scope.token.replace(/^"(.+(?="$))"$/, '$1') }
                                }).then(function mySuccess(response) {
                                    console.log(response.status + ": OK. Successfully deleted '" +
                                        $scope.ws + ":" + $scope.ds + "'");
                                    $route.reload();
                                    splash.close();
                                }, function myError(response) {
                                    console.log(response.statusText);
                                    splash.close();
                                });

                            }
                        });

                    }).on('shown.bs.modal', function (e) {
                        if ($('#prompt').length > 0) {
                            $('#prompt').focus();
                        }
                    }).modal('show');
                }

                _show();
                return deferredObject.promise();

            }



                }])

        .controller('SplashCtrl', ['$scope', '$modalInstance', 'msg', 'layer_title',
            function ($scope, $modalInstance, msg, layer_title) {
            $scope.msg = msg;
            $scope.layer_title = layer_title;

        }])


        .controller('downloadWindowCtrl', ['$scope', '$interval', '$modalInstance', '$modal', '$http',
            '$window', '$translate', 'url', 'layer_title', 'msg',
            function ($scope, $interval, $modalInstance, $modal, $http, $window, $translate, url, layer_title, msg) {
                $scope.url = url;
                $scope.layer_title = layer_title;
                $scope.msg = msg;

                var lock =0;

                $scope.refreshView = $interval(function(){
                            $http({
                                method : "GET",
                                url : url
                            }).then(function mySucces(response) {
                                if(response.status=='202') {
                                    console.log("Waiting... " + response.data);// Accepted: wait
                                } else if(response.status=='200') {
                                    stopSuccess();
                                }else {
                                    console.log("Server response: " + response);
                                }
                            }, function myError(response) {
                                stopError();
                            });

                },1000);


                function stopSuccess() {
                    $interval.cancel($scope.refreshView);
                    if (lock == 0) { // We guard this to ensure the file is not downloaded multiple times
                        lock = 1;
                        console.log("Downloading file...");
                        window.open(url, '_blank');
                        $modalInstance.close();
                    }
                }


                function stopError() {
                    $interval.cancel($scope.refreshView);

                    if (lock == 0) {
                        lock = 1;

                        console.error("Error:" + response.data);
                        $modalInstance.close();

                        var modalError = $modal.open({
                            templateUrl: 'resources/errorPopup.html',
                            controller: 'ErrorPopupCtrl',
                            resolve: {
                                title: function () {
                                    return $translate("MANAGE_LAYERS.SERVER_ERROR");
                                },
                                text: function () {
                                    return response.data;
                                }
                            },
                        });
                    }
                }



            }])


    .controller('ErrorPopupCtrl', ['$scope','$modalInstance', 'title', 'text', function ($scope, $modalInstance,
                                                                                         title, text) {
        $scope.title = title;
        $scope.text=text;
        $scope.close = function () {
            $modalInstance.dismiss('close');
        };
    }])

    .controller('PopupCtrl', ['$scope','$modalInstance', 'jsonresp', function ($scope, $modalInstance, jsonresp) {
        $scope.details = jsonresp.data;
        $scope.layerTitle=jsonresp.data.title;

        $scope.close = function () {
            $modalInstance.dismiss('close');
        };

    }]);

})();