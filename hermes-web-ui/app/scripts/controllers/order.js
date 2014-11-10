'use strict';

angular.module('hermes.ui').controller('OrderCtrl', function ($scope, $log, $alert, ConfigurationSvc, FormSvc, PrinterSvc) {
    $scope.printing = false;

    $scope.params = {};

    $scope.getForm = function(name) {
        FormSvc.get(name).success(function(data) {
            $scope.frm = data;
            angular.forEach($scope.frm.fields, function(field) {
                if(field) {
                    var val = field.type==='BOOLEAN' ? (field.defaultValue==='true') : field.defaultValue;
                    $scope.params[field.name] = val;
                }
            });
        });
    };

    $scope.query = function() {
        $scope.params['_form'] = $scope.configuration['hermes.orders.form'];
        FormSvc.query($scope.params).success(function(data) {
            $scope.orders = data;
        });
    };

    $scope.select = function(selected) {
        angular.forEach($scope.orders, function(order) {
            order._selected = selected;
        });
    };

    $scope.doPrint = function(order, type) {
        return PrinterSvc.print({orderId: order.orderId, type: type}).success(function(data) {
            $alert({content: 'Printed order: ' + order.orderId + ' (' + type + ')', placement: 'top', type: 'success', show: true, duration: 15});
            switch(type) {
                case 'INVOICE':
                    order._invoiceSuccess = true;
                    break;
                case 'LABEL':
                    order._labelSuccess = true;
                    break;
            }
        }).error(function(data) {
            $alert({content: 'Printing order: ' + order.orderId + ' (' + type + ') failed!', placement: 'top', type: 'danger', show: true, duration: 15});
            $log.error(data);
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

    var iterator = 0;

    var printNext = function() {
        if($scope.orders[iterator]) {
            if($scope.orders[iterator]._selected) {
                $log.info('Printing: ' + $scope.orders[iterator].orderId + ' (invoice)');
                $scope.doPrint($scope.orders[iterator], 'INVOICE').then(function() {
                    $log.info('Printing: ' + $scope.orders[iterator].orderId + ' (label)');
                    $scope.doPrint($scope.orders[iterator], 'LABEL').then(function() {
                        $log.info('Printing: end!');
                        iterator++;
                        printNext();
                    });
                });

            } else {
                iterator++;
                printNext();
            }
        }
    };

    $scope.print = function() {
        // TODO: check invoice ID and shipping ID exist
        // TODO: print report
        // TODO: check charge (see above)
        iterator = 0;
        printNext();
    };

    ConfigurationSvc.list().success(function(data) {
        $scope.configuration = data.properties;

        // TODO: use configured form
        $scope.getForm('orders');
    });
});
