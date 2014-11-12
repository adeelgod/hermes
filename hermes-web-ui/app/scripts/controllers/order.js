'use strict';

angular.module('hermes.ui').controller('OrderCtrl', function ($scope, $log, $alert, ConfigurationSvc, FormSvc, PrinterSvc) {
    $scope.printing = false;

    $scope.params = {_order_ids: []};

    $scope.querying = false;

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

    $scope.charge = function(index) {
        var chargeSize = $scope.configuration['hermes.charge.size']
        return Math.ceil( (index+1)/chargeSize);
    };

    $scope.query = function() {
        $scope.params['_form'] = $scope.configuration['hermes.orders.form'];
        $scope.querying = true;
        $scope.orders = null;
        FormSvc.query($scope.params).success(function(data) {
            $scope.querying = false;
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
    var count = 0;

    var printNext = function() {
        if($scope.orders[iterator]) {
            if($scope.orders[iterator]._selected) {
                count++;
                // TODO: check invoice ID and shipping ID exist
                $log.info('Printing: ' + $scope.orders[iterator].orderId + ' (invoice)');

                $scope.params._order_ids.push($scope.orders[iterator].orderId);

                $scope.doPrint($scope.orders[iterator], 'INVOICE').then(function() {
                    $log.info('Printing: ' + $scope.orders[iterator].orderId + ' (label)');
                    $scope.doPrint($scope.orders[iterator], 'LABEL').then(function() {
                        var chargeSize = $scope.configuration['hermes.charge.size']

                        if( (count+1)%chargeSize===0) {
                            $scope.printReport().then(function() {
                                $log.info('Printing: charge!');
                                $scope.params._order_ids = [];
                                iterator++;
                                printNext();
                            });
                        } else {
                            $log.info('Printing: ---');
                            iterator++;
                            printNext();
                        }
                    });
                });

            } else {
                iterator++;
                printNext();
            }
        } else {
            $scope.printing = false;
        }

        if(iterator>=$scope.orders.length) {
            $scope.printing = false;
        }
    };

    $scope.print = function() {
        iterator = 0;
        $scope.printing = true;
        printNext();
    };

    $scope.printReport = function() {
        var params = angular.copy($scope.params);
        params.type = 'REPORT';
        params._templates = $scope.configuration['hermes.reporting.template.report'];

        return PrinterSvc.print(params).success(function(data) {
            $alert({content: 'Printed report: ' + params._templates + ' (REPORT)', placement: 'top', type: 'success', show: true, duration: 15});
        }).error(function(data) {
            $alert({content: 'Printed report: ' + params._templates + ' (REPORT)', placement: 'top', type: 'danger', show: true, duration: 15});
            $log.error(data);
        });
    };

    ConfigurationSvc.list().success(function(data) {
        $scope.configuration = data.properties;

        $scope.getForm($scope.configuration['hermes.orders.form'])
    });
});
