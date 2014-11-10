'use strict';

angular.module('hermes.ui').controller('OrderCtrl', function ($scope, $alert, ConfigurationSvc, FormSvc) {
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

    ConfigurationSvc.list().success(function(data) {
        $scope.configuration = data.properties;

        // TODO: use configured form
        $scope.getForm('orders');
    });
});
