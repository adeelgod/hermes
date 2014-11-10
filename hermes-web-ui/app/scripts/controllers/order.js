'use strict';

angular.module('hermes.ui').controller('OrderCtrl', function ($scope, $alert, PrinterLogSvc, ConfigurationSvc, FormSvc) {
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

    // deprecated
    $scope.print = function() {
        // TODO: implement this
        $alert({content: 'Print scheduled', placement: 'top', type: 'success', show: true, duration: 3});
        angular.forEach($scope.orders, function(order) {
            if(order._selected) {
                $alert({content: 'Printing order: ' + order.orderId, placement: 'top', type: 'success', show: true, duration: 3});
            }
        });
        /**
        $scope.printing = true;
        PrinterLogSvc.print().success(function(data) {
            $alert({content: 'Print scheduled', placement: 'top', type: 'success', show: true, duration: 3});
            $scope.printing = false;
        }).error(function(data) {
            $alert({content: 'Print failed', placement: 'top', type: 'danger', show: true, duration: 3});
            $scope.printing = false;
        });
         */
    };

    $scope.pages = function() {
        var p = [];

        if($scope.orders) {
            for(var i=1; i<=$scope.orders.totalPages; i++) {
                p.push(i);
            }
        }

        return p;
    };

    $scope.go = function(p) {
        PrinterLogSvc.list({page: p-1, from: $scope.fromDate, until: $scope.untilDate}).success(function(data) {
            $scope.orders = data;
        });
    };

    $scope.isCurrentPage = function(p) {
        if($scope.orders) {
            return $scope.orders.number === p-1;
        }

        return false;
    };

    ConfigurationSvc.list().success(function(data) {
        $scope.configuration = data.properties;

        // TODO: use configured form
        $scope.getForm('orders');
    });
});
