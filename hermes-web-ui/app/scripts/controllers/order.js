(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.controller');
    } catch (e) {
        module = angular.module('hermes.ui.controller', []);
    }

    module.controller('OrderCtrl', function ($scope, $interval, $log, $alert, $modal, ConfigurationSvc, FormSvc, PrinterSvc) {
        $scope.params = {_order_ids: []};

        $scope.busy = false;

        $scope.loading = true;

        $scope.selectedOrders = [];

        $scope.currentOrder = {};

        $scope.currentOrder = {};

        $scope.formPrefix = '';

        $scope.active_target = '';

        var confirmModal = $modal({title: 'Order print files missing', content: 'Are you sure you want to proceed?', scope: $scope, template: 'parts/confirm.html', show: false, placement: 'center'});
        confirmModal.$scope.confirm = function() {
            confirmModal.hide();
            $scope.print();
        };
        confirmModal.$scope.close = function() {
            confirmModal.hide();
        };

        $scope.init = function(formPrefix) {
            $scope.formPrefix = formPrefix;
        }

        $scope.checkBeforePrint = function() {
            var execute = true;

            for(var i=0; i<$scope.orders.length; i++) {
                var order = $scope.orders[i];
                if(order._selected && (!order._invoiceExists || !order._labelExists)) {
                    confirmModal.$promise.then(confirmModal.show);
                    execute = false;
                    break;
                }
            }

            if(execute) {
                confirmModal.$scope.confirm();
            }
        };

        $scope.getForm = function(name) {
            FormSvc.get(name).success(function(data) {
                $scope.frm = data;
                angular.forEach($scope.frm.fields, function(field) {
                    if(field && field.parameter) {
                        var val = field.fieldType==='BOOLEAN' ? (field.defValue==='true') : field.defValue;
                        $scope.params[field.name] = val;

                        if(field.name==='from') {
                            $scope.params['from'] = moment().startOf('day');
                        } else if(field.name==='until') {
                            $scope.params['until'] = moment().startOf('day').add(24, 'hours');
                        }
                    }
                });
                $scope.loading = false;
            });
        };

        $scope.charge = function(index) {
            var chargeSize = Number($scope.configuration['hermes.charge.size']);
            return Math.ceil( (index+1)/chargeSize);
        };

        $scope.query = function() {

            $scope.selectedOrders = [];

            const formKey = $scope.getFormKey();
            $scope.params['_form'] = $scope.configuration[formKey];
            $scope.params['_checkFiles'] = true;
            $scope.params['_downloadFiles'] = true;
            $scope.busy = true;
            $scope.orders = null;
            FormSvc.query($scope.params).success(function(data) {
                $scope.busy = false;
                $scope.orders = data;
                angular.forEach($scope.orders, function(order) {
                    order._selected = true;
                });
            }).error(function(data) {
                $scope.busy = false;
                $alert({content: 'Query failed! Check input parameters.', placement: 'top', type: 'danger', show: true, duration: 5});
            });
        };

        $scope.select = function(selected) {
            angular.forEach($scope.orders, function(order) {
                order._selected = selected;
            });
        };

        $scope.selectEntry = function(entry) {
            entry._selected=!entry._selected;

            if(entry._selected) {
                $scope.selectedOrders.push(entry);
            } else {
                for(var i=0; i<$scope.selectedOrders.length; i++) {
                    if(entry.orderId===$scope.selectedOrders[i].orderId) {
                        $scope.selectedOrders.splice(i, 1);
                        break;
                    }
                }
            }
        };

        var move = function (ar, old_index, new_index) {
            if (new_index >= ar.length) {
                var k = new_index - ar.length;
                while ((k--) + 1) {
                    ar.push(undefined);
                }
            }
            ar.splice(new_index, 0, ar.splice(old_index, 1)[0]);
        };

        $scope.moveUp = function(index) {
            if(index>0) {
                move($scope.orders, index, index-1);
            }
        };

        $scope.moveDown = function(index) {
            if(index<$scope.orders.length-1) {
                move($scope.orders, index, index+1);
            }
        };

        // TODO: remove
        $scope.doPrint = function(order, type) {
            return PrinterSvc.print({orderId: order.orderId, type: type}).success(function(data) {
                $alert({content: 'Printed order: ' + order.orderId + ' (' + type + ')', placement: 'top', type: 'success', show: true, duration: 5});
                switch(type) {
                    case 'INVOICE':
                        order._invoiceSuccess = true;
                        break;
                    case 'LABEL':
                        order._labelSuccess = true;
                        break;
                }
            }).error(function(data) {
                $alert({content: 'Printing order: ' + order.orderId + ' (' + type + ') failed!', placement: 'top', type: 'danger', show: true, duration: 5});
                switch(type) {
                    case 'INVOICE':
                        order._invoiceSuccess = false;
                        break;
                    case 'LABEL':
                        order._labelSuccess = false;
                        break;
                }
            });
        };

        // TODO: remove
        var iterator = -1;
        var count = 0;

        // TODO: remove
        var printNext = function(skipIteration) {
            if(!skipIteration) {
                iterator++;
            }
            if(iterator<$scope.orders.length && $scope.orders[iterator]) {
                if($scope.orders[iterator]._selected===true) {

                    var chargeSize = Number($scope.configuration['hermes.charge.size']);

                    // first print charge report
                    if( !skipIteration && count%chargeSize===0 ) {
                        if(iterator<$scope.selectedOrders.length) {
                            $scope.printReport().then(function() {
                                $log.info('######## REPORT  order: ' +  $scope.orders[iterator].orderId + ' count: ' + count + ' iterator: ' + iterator);
                                printNext(true);
                            });
                        } else {
                            //count++;
                            printNext(true);
                        }
                    } else {
                        // TODO: check invoice ID and shipping ID exist
                        $scope.currentOrder.orderId = $scope.orders[iterator].orderId;
                        $scope.doPrint($scope.orders[iterator], 'INVOICE').then(function() {
                            $log.info('######## INVOICE order: ' +  $scope.orders[iterator].orderId + ' count: ' + count + ' iterator: ' + iterator);

                            $scope.currentOrder.invoiceId = $scope.orders[iterator].invoiceId;
                            $scope.doPrint($scope.orders[iterator], 'LABEL').then(function() {
                                $log.info('######## LABEL   order: ' +  $scope.orders[iterator].orderId + ' count: ' + count + ' iterator: ' + iterator);

                                $scope.currentOrder.shippingId = $scope.orders[iterator].shippingId;
                                count++;
                                printNext();
                            });
                        });
                    }
                } else {
                    printNext();
                    $scope.currentOrder.orderId = {};
                    $scope.currentOrder.orderId = null;
                    $scope.currentOrder.invoiceId = null;
                    $scope.currentOrder.shippingId = null;
                }
            } else {
                $scope.busy = false;
                iterator = -1;
                count = 0;
                $scope.currentOrder.orderId = {};
                $scope.currentOrder.orderId = null;
                $scope.currentOrder.invoiceId = null;
                $scope.currentOrder.shippingId = null;
            }
        };

        $scope.printCancel = function() {
            PrinterSvc.cancel().success(function(data) {
                $scope.busy = false;
                if($scope.printStatusLoop) {
                    $interval.cancel($scope.printStatusLoop);
                    $scope.printStatusLoop = undefined;
                }
                $alert({content: 'Printing cancelled!', placement: 'top', type: 'danger', show: true, duration: 5});
            }).error(function(data) {
                // TODO: not sure about that
                //$scope.busy = false;
                //$alert({content: 'Error while printing documents', placement: 'top', type: 'danger', show: true, duration: 5});
            });
        };

        $scope.print = function() {
            // TODO: remove
            iterator = -1;
            count = 0;

            $scope.busy = true;

            // new approach
            var req = {target: $scope.active_target, charges: [], chargeSize: $scope.configuration['hermes.charge.size']};

            var prevCharge = 0;
            var charge = {};

            for(var i=0; i<$scope.orders.length; i++) {
                var curCharge = $scope.charge(i);
                if(curCharge!==prevCharge) {
                    charge = {};
                    charge.pos = curCharge;
                    charge.orders = [];
                    req.charges.push(charge);
                    prevCharge = curCharge;
                }
                if($scope.orders[i] && $scope.orders[i]._selected) {
                    charge.orders.push($scope.orders[i].orderId);
                }
            }

            PrinterSvc.printAll(req).success(function(data) {
                //$scope.busy = false;
                $alert({content: 'Documents queued', placement: 'top', type: 'success', show: true, duration: 5});
                $scope.printStatusLoop = $interval(function() {
                    PrinterSvc.status().success(function(data) {
                        $scope.busy = Boolean(data);
                        if(!$scope.busy) {
                            $interval.cancel($scope.printStatusLoop);
                            $scope.printStatusLoop = undefined;
                            $alert({content: 'Print queue empty.', placement: 'top', type: 'success', show: true});
                        }
                    }).error(function(data) {
                        // TODO: not sure about that
                        //$scope.busy = false;
                        //$alert({content: 'Error while printing documents', placement: 'top', type: 'danger', show: true, duration: 5});
                    });
                }, 5000); // TODO: configurable?
            }).error(function(data) {
                $scope.busy = false;
                $alert({content: 'Error while printing documents', placement: 'top', type: 'danger', show: true, duration: 5});
            });
            //printNext();
        };

        // TODO: remove
        $scope.printReport = function() {
            var params = angular.copy($scope.params);
            params.type = 'REPORT';
            params._order_ids = [];
            params._templates = $scope.configuration['hermes.reporting.template.report'];

            var chargeSize = Number($scope.configuration['hermes.charge.size']);
            var start = chargeSize*Math.floor(count/chargeSize);
            var end = start+chargeSize;
            $log.info('######## start: ' + start + ' end: ' + end + ' #' + count + ' - ' + (count%chargeSize));
            for(var i=start; i<end; i++) {
                if($scope.orders[i]._selected) {
                    params._order_ids.push($scope.orders[i].orderId);
                }
            }

            return PrinterSvc.print(params).success(function(data) {
                $alert({content: 'Printed report: ' + params._templates + ' (REPORT)', placement: 'top', type: 'success', show: true, duration: 5});
            }).error(function(data) {
                $alert({content: 'Printed report: ' + params._templates + ' (REPORT)', placement: 'top', type: 'danger', show: true, duration: 5});
                $log.error(data);
            });
        };

        $scope.getFormKey = function() {
            if($scope.active_target == '') {
                $scope.active_target = 1;
            }
            var key = "hermes.print." + $scope.active_target;
            if($scope.formPrefix != '') {
                key += "." + $scope.formPrefix;
            }
            return key +  ".SQL_hermes_form_name";
        }

        ConfigurationSvc.list().success(function(data) {
            $scope.loading = true;

            $scope.configuration = data.properties;

            const formKey = $scope.getFormKey();

            $scope.getForm($scope.configuration[formKey]);
        });

        //FormSvc.synchronize($scope);

        //$alert({content: 'Hi Daniel. Die Navigationsleiste ist jetzt umgestaltet. Melde Dich als Admin an, um alle Buttons zu sehen.', placement: 'top', type: 'info', show: true});
    });
})();
