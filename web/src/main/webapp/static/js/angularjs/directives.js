'use strict';

/* Directives */

angular.module('ogpHarvester.directives', []).directive('appVersion', ['version',
    function (version) {
        return function (scope, elm, attrs) {
            elm.text(version);
        };
    }
])
    .directive('datetimepicker', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attrs, ngModelCtrl) {
                $(function () {
                    element.datetimepicker({
                        pickTime: false
                    }).on('changeDate', function (event) {
                        ngModelCtrl.$setViewValue($(event.currentTarget).find("input").val());
                        scope.$apply();
                    });

                    scope.$on("$destroy", function () {
                        console.log("Destroying datetimepicker");
                        element.data('datetimepicker').destroy();
                    });
                });

            }
        }
    })
    .directive('multiselectDropdown', ['$parse',
        function ($parse) {
            return {
                requires: 'ngModel',
                link: function ($scope, element, attributes) {

                    element = $(element[0]); // Get the element as a jQuery element
                    var placeholder = $parse(element.data()['placeholder']);


                    // Below setup the dropdown:

                    element.multiselect({
                        buttonText: function (options) {
                            if (options.length == 0) {
                                return placeholder() + ' <b class="caret"></b>';
                            } else if (options.length > 3) {
                                return options.first().text() + ' + ' + (options.length - 1) + ' more selected <b class="caret"></b>';
                            } else {
                                var selected = '';
                                options.each(function () {
                                    var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();

                                    selected += label + ', ';
                                });
                                return selected.substr(0, selected.length - 2) + ' <b class="caret"></b>';
                                //return options.first().text() + ' <b class="caret"></b>';
                            }
                        },
                        // Replicate the native functionality on the elements so
                        // that angular can handle the changes for us.
                        onChange: function (optionElement, checked) {
                            optionElement.removeAttr('selected');
                            if (checked) {
                                optionElement.attr('selected', 'selected');
                            }
                            element.change();
                        }

                    });
                    // Watch for any changes to the length of our select element
                    $scope.$watch(function () {
                        return element[0].length;
                    }, function () {
                        element.multiselect('rebuild');
                    });

                    // Watch for any changes from outside the directive and refresh
                    $scope.$watch(attributes.ngModel, function (newVal, oldVal) {
                        element.multiselect('refresh');
                    });

                    $scope.$on('$destroy', function () {
                        console.log("destroying multilselect");
                        element.multiselect('destroy');
                    });

                    // Below maybe some additional setup
                }
            }
        }

    ]);