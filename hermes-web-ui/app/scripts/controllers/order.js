'use strict';

angular.module('hermes.ui').controller('OrderCtrl', function ($scope, $log, $alert, ConfigurationSvc, FormSvc, PrinterSvc) {
    $scope.params = {_order_ids: []};

    $scope.busy = false;

    $scope.loading = true;

    $scope.getForm = function(name) {
        FormSvc.get(name).success(function(data) {
            $scope.frm = data;
            angular.forEach($scope.frm.fields, function(field) {
                if(field) {
                    var val = field.fieldType==='BOOLEAN' ? (field.defValue==='true') : field.defValue;
                    $scope.params[field.name] = val;
                }
            });
            $scope.loading = false;
        });
    };

    $scope.charge = function(index) {
        var chargeSize = $scope.configuration['hermes.charge.size'];
        return Math.ceil( (index+1)/chargeSize);
    };

    $scope.query = function() {
        $scope.params['_form'] = $scope.configuration['hermes.orders.form'];
        $scope.params['_checkFiles'] = true;
        $scope.params['_downloadFiles'] = true;
        $scope.busy = true;
        $scope.orders = null;
        FormSvc.query($scope.params).success(function(data) {
            $scope.busy = false;
            $scope.orders = data;
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

    var iterator = -1;
    var count = 0;

    var printNext = function(skipIteration) {
        if(!skipIteration) {
            iterator++;
        }
        if($scope.orders[iterator]) {
            if($scope.orders[iterator]._selected) {

                //$scope.params._order_ids.push($scope.orders[iterator].orderId);

                var chargeSize = $scope.configuration['hermes.charge.size'];

                // first print charge report
                if( count%(chargeSize+1)===0) {
                    $scope.printReport().then(function() {
                        $log.info('Printing: charge!');
                        //iterator++;
                        count++;
                        printNext(true);
                    });
                } else {
                    // TODO: check invoice ID and shipping ID exist
                    $scope.doPrint($scope.orders[iterator], 'INVOICE').then(function() {
                        $log.info('Printed: ' + $scope.orders[iterator].orderId + ' (invoice)');
                        $scope.doPrint($scope.orders[iterator], 'LABEL').then(function() {
                            $log.info('Printed: ' + $scope.orders[iterator].orderId + ' (label)');
                            //iterator++;
                            count++;
                            printNext();
                        });
                    });
                }
            } else {
                //iterator++;
                printNext();
            }
        } else {
            $scope.busy = false;
        }

        if(iterator>=$scope.orders.length) {
            $scope.busy = false;
        }
    };

    $scope.print = function() {
        iterator = -1;
        count = 0;
        $scope.busy = true;
        printNext();
    };

    $scope.printReport = function() {
        var params = angular.copy($scope.params);
        params.type = 'REPORT';
        params._order_ids = [];
        params._templates = $scope.configuration['hermes.reporting.template.report'];

        var chargeSize = $scope.configuration['hermes.charge.size'];
        var start = chargeSize*count;
        var end = chargeSize*count+chargeSize;
        $log.info('######## start: ' + start + ' end: ' + end);
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

    ConfigurationSvc.list().success(function(data) {
        $scope.loading = true;

        $scope.configuration = data.properties;

        $scope.getForm($scope.configuration['hermes.orders.form']);
    });

    $alert({content: 'Hi Daniel. Die Navigationsleiste ist jetzt umgestaltet. Melde Dich als Admin an, um alle Buttons zu sehen.', placement: 'top', type: 'info', show: true});
});
