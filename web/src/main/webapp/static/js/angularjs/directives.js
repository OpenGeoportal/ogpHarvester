'use strict';

/* Directives */

angular.module('ogpHarvester.directives', []).directive('appVersion', ['version',
    function(version) {
        return function(scope, elm, attrs) {
            elm.text(version);
        };
    }
])
    .directive('datetimepicker', ['$log',
        function($log) {
            return {
                restrict: 'A',
                require: 'ngModel',
                scope: {
                    thisDate: '=ngModel',
                    beforeDate: '=ogpCheckAfter'
                },
                link: function(scope, element, attrs, ngModelCtrl) {
                    $(function() {
                        element.datetimepicker({
                            pickTime: false,
                            format: 'MM/dd/yyyy'
                        }).datetimepicker('setValue', scope.thisDate).on('changeDate', function(event) {
                            //ngModelCtrl.$setViewValue($(event.currentTarget).find("input").val());
                            var date = event.date;
                            if (date) {
                                date.setHours(0, 0, 0, 0);
                            }
                            ngModelCtrl.$setViewValue(date);
                            //ngModelCtrl.$modelValue = event.date;
                            scope.$apply();
                        });

                        scope.$on("$destroy", function() {
                            element.data('datetimepicker').destroy();
                        });
                    });

                    /* global -nullFormCtrl */
                    var nullFormCtrl = {
                        $addControl: angular.noop,
                        $removeControl: angular.noop,
                        $setValidity: angular.noop,
                        $setDirty: angular.noop,
                        $setPristine: angular.noop
                    };
                    var modelCtrl = ngModelCtrl,
                        formCtrl = element.inheritedData('$formController') || nullFormCtrl;

                    formCtrl.$addControl(modelCtrl);


                    scope.$on('$destroy', function() {
                        formCtrl.$removeControl(modelCtrl);
                        scope = {};
                    });

                    if (scope.thisDate) {
                        ngModelCtrl.$dirty = true;
                    }

                    var validate = function(otherDate, thisDate, ctrl) {
                        if (otherDate && thisDate && (otherDate > thisDate)) {
                            // mark error
                            $log.log("Invalid after date");
                            ctrl.$setValidity('dateAfter', false);
                        } else {
                            // mark as valid
                            $log.log("Valid after date");
                            ctrl.$setValidity('dateAfter', true);
                        }

                    };

                    if (attrs.ogpCheckAfter) {
                        scope.$watch('thisDate', function(newValue, oldValue, scope) {
                            $log.log(attrs.ngModel + " has changed to " + newValue);
                            validate(scope.beforeDate, scope.thisDate, ngModelCtrl);
                        });

                        scope.$watch('beforeDate', function(newValue, oldValue, scope) {
                            $log.log(attrs.ogpCheckAfter + " has changed to " + newValue);
                            validate(scope.beforeDate, scope.thisDate, ngModelCtrl);
                        });
                    }
                }
            };
        }
    ])
    .directive('ogpCheckAfter2', ['$log', '$filter',
        function($log, $filter) {
            var f = $filter;
            return {
                restrict: 'A',
                require: 'ngModel',

                link: function(scope, element, attrs, ngModelCtrl) {
                    var otherDate = attrs.ogpCheckAfter2;
                    ngModelCtrl.$parsers.push(function(viewValue) {
                        if (validate(scope.$eval(attrs.ogpCheckAfter2), viewValue, ngModelCtrl)) {
                            return viewValue;
                        } else {
                            return viewValue;
                        }
                    });
                    ngModelCtrl.$formatters.push(function(modelValue) {
                        if (validate(scope.$eval(attrs.ogpCheckAfter2), modelValue, ngModelCtrl)) {
                            return modelValue;
                        } else {
                            return modelValue;
                        }
                    });

                    var validate = function(otherDate, thisDate, ctrl) {
                        var otherD = parseDate(otherDate);
                        var thisD = parseDate(thisDate);
                        if (otherD && thisD && (otherD > thisD)) {
                            // mark error
                            $log.log("Invalid after date");
                            ctrl.$setValidity('dateAfter', false);
                            ctrl.$dirty = true;
                            return false;
                        } else {
                            // mark as valid
                            $log.log("Valid after date");
                            ctrl.$setValidity('dateAfter', true);
                            return true;
                        }

                    };

                    var parseDate = function(value) {
                        if (!value) {
                            return null;
                        } else if (angular.isDate(value)) {
                            return value;
                        } else if (angular.isString(value)) {
                            var date = moment(value, "MM/DD/YYYY");
                            if (isNaN(date)) {
                                return undefined;
                            } else {
                                return date;
                            }
                        } else {
                            return undefined;
                        }
                    }

                    scope.$watch(attrs.ogpCheckAfter2, function(newValue, oldValue, scope) {
                        $log.log(attrs.ogpCheckAfter2 + " has changed to " + newValue);
                        validate(newValue, scope.$eval(attrs.ngModel), ngModelCtrl);
                    });

                }
            };
        }
    ]);
