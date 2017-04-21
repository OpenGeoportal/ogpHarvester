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

		.controller('ManageLayersCtrl', function($scope, $http, $translate, $modal) {

            $scope.itemsByPage = 12;
            $scope.jsonresult = [];
            $scope.displayedCollection = [].concat($scope.jsonresult);
            $scope.actions = [$translate("MANAGE_LAYERS.DOWNLOAD"), $translate("MANAGE_LAYERS.UPDATE"),
                $translate("MANAGE_LAYERS.DELETE")];

            $scope.GetValue = function (action, row_name) {
                //console.log(action + " " + row_name);
                var res = row_name.split(":");
                $scope.ws=res[0];
                $scope.ds= res[1];
                switch(action) {
                    case $translate("MANAGE_LAYERS.DOWNLOAD"):
                        console.log("download " + "http://localhost:8083/workspaces/"+
                            $scope.ws + "/datasets/" + $scope.ds + "/download");
                    case $translate("MANAGE_LAYERS.UPDATE"):
                            //TODO: move this to the upload tab ?
                    case $translate("MANAGE_LAYERS.DELETE"):
                        confirmDlg();
                        }

            };

            $http({
                method : "GET",
                //url : "http://localhost:8083/allDatasets",
                url : "http://localhost:8083/workspaces/db/datasets",
                isArray: true
            }).then(function mySuccess(response) {
                $scope.jsonresult = response.data;
                //$scope.dispPages = Math.round(response.data.totalElements/$scope.itemsByPage + 0.5);
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
                });
                    var modalInstance = $modal.open({
                    templateUrl: 'resources/popup.html',
                    controller: 'PopupCtrl',
                    resolve: {
                        jsonresp:function(){
                            return $http({
                                method : "GET",
                                url : "http://localhost:8083/workspaces/" + $scope.ws + "/datasets/" + $scope.ds,
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
                    //var calbackParam = "";

                    var btnhtml = '<button id="ezok-btn" class="btn btn-primary">' + defaults.noButtonText + '</button>';

                    if (defaults.yesButtonText && defaults.yesButtonText.length > 0) {
                        btnhtml += '<button id="ezclose-btn" class="btn btn-default">' + defaults.yesButtonText + '</button>';
                    }
                    $('#ezAlerts-footer').html(btnhtml).on('click', 'button', function (e) {
                        if (e.target.id === 'ezok-btn') {//false
                            //calbackParam = false;
                            //console.log("cancel");
                            $('#ezAlerts').modal('hide');
                        } else if (e.target.id === 'ezclose-btn') {//true
                            //calbackParam = true;
                            //console.log("ok");
                            $('#ezAlerts').modal('hide');
                        }
                        //$('#ezAlerts').modal('hide');
                        //console.log(calbackParam);
                    });

                    $('#ezAlerts').modal({
                        show: false,
                        backdrop: backd,
                        keyboard: keyb
                    }).on('hidden.bs.modal', function (e) {
                        $('#ezAlerts').remove();
                        deferredObject.resolve(calbackParam);
                    }).on('shown.bs.modal', function (e) {
                        if ($('#prompt').length > 0) {
                            $('#prompt').focus();
                        }
                    }).modal('show');
                }

                _show();
                return deferredObject.promise();
            }



		})


    .controller('PopupCtrl', ['$scope','$modalInstance', 'jsonresp', function ($scope, $modalInstance, jsonresp) {
        $scope.details = jsonresp.data;
        $scope.layerTitle=jsonresp.data.title;

        $scope.close = function () {
            $modalInstance.dismiss('close');
        };

    }]);





})();