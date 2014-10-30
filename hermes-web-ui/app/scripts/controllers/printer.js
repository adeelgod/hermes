'use strict';

angular.module('hermes.ui').controller('PrinterCtrl', function ($scope, PrinterSvc) {
    PrinterSvc.printers().success(function(data) {
        $scope.printers = data;
    });
});
