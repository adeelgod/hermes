'use strict';

angular.module('hermes.ui').controller('OrderCtrl', function ($scope, PrinterLogSvc) {
    $scope.search = function() {
        PrinterLogSvc.list({page: 0}).success(function(data) {
            $scope.orders = data;
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
        PrinterLogSvc.list({page: p-1}).success(function(data) {
            $scope.orders = data;
        });
    };

    $scope.isCurrentPage = function(p) {
        if($scope.orders) {
            return $scope.orders.number === p-1;
        }

        return false;
    };

    $scope.next = function() {
        PrinterLogSvc.list({page: $scope.orders.number+1}).success(function(data) {
            $scope.orders = data;
        });
    };

    $scope.previous = function() {
        PrinterLogSvc.list({page: $scope.orders.number-1}).success(function(data) {
            $scope.orders = data;
        });
    };
});
