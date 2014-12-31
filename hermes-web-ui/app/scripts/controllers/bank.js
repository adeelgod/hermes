'use strict';

angular.module('hermes.ui').controller('BankCtrl', function ($scope, $alert, BankSvc) {
    $scope.busy = false;
    $scope.loading = true;

    // TODO: implement this

    $scope.getBankStatements = function() {
        $scope.loading = true;
        BankSvc.list().success(function(data) {
            $scope.bankStatements = data;
            $scope.loading = false;
        }).error(function(data) {
            $alert({content: 'Error retrieving open bank statements.', placement: 'top', type: 'danger', show: true, duration: 5});
        });
    };

    $scope.select = function(selected) {
        angular.forEach($scope.bankStatements, function(bankStatement) {
            bankStatement._selected = selected;
        });
    };

    $scope.getBankStatements();
});
