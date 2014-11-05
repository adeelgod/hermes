'use strict';

angular.module('hermes.ui').controller('OrderCtrl', function ($scope, $alert, $log, PrinterLogSvc, ConfigurationSvc, FormSvc) {
    $scope.printing = false;

    $scope.params = {};

    $scope.getForm = function(name) {
        $log.debug('Getting form...' + name);
        FormSvc.get(name).success(function(data) {
            $scope.frm = data;
            $alert({content: 'Got form...', placement: 'top', type: 'success', show: true, duration: 2});
        });
    };

    // deprecated
    $scope.search = function() {
        PrinterLogSvc.list({page: 0, from: $scope.fromDate, until: $scope.untilDate}).success(function(data) {
            $scope.orders = data;
        });
    };

    // deprecated
    $scope.select = function() {
        PrinterLogSvc.select({from: $scope.fromDate, until: $scope.untilDate, selected: true}).success(function(data) {
            $alert({content: 'Entries selected: ' + data, placement: 'top', type: 'success', show: true, duration: 3});
            $scope.search();
        });
    };

    // deprecated
    $scope.unselect = function() {
        PrinterLogSvc.select({from: $scope.fromDate, until: $scope.untilDate, selected: false}).success(function(data) {
            //$scope.selected = data;
            $alert({content: 'Entries unselected: ' + data, placement: 'top', type: 'success', show: true, duration: 3});
            $scope.search();
        });
    };

    // deprecated
    $scope.print = function() {
        $scope.printing = true;
        PrinterLogSvc.print().success(function(data) {
            $alert({content: 'Print scheduled', placement: 'top', type: 'success', show: true, duration: 3});
            $scope.printing = false;
        }).error(function(data) {
            $alert({content: 'Print failed', placement: 'top', type: 'danger', show: true, duration: 3});
            $scope.printing = false;
        });
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
        $scope.configuration = data;

        // TODO: use configured form
        $log.debug('Calling form...');
        $scope.getForm('orders');
    });
});
