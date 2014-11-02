'use strict';

angular.module('hermes.ui').controller('OrderCtrl', function ($scope, PrinterLogSvc) {
    $scope.search = function() {
        PrinterLogSvc.list().success(function(data) {
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
});
