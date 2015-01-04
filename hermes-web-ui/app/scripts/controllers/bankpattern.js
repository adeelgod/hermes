'use strict';

angular.module('hermes.ui').controller('BankPatternCtrl', function ($rootScope, $scope, $stateParams, $alert, BankPatternSvc) {
    $scope.loading = true;
    $scope.pattern = {};

    $scope.list = function() {
        $scope.loading = true;
        return BankPatternSvc.list().success(function(data) {
            $scope.patterns = data;
            $scope.loading = false;
        });
    };

    $scope.save = function() {
        $scope.loading = true;
        BankPatternSvc.save($scope.pattern).success(function(data) {
            $scope.pattern = data;
            $scope.list();
        });
    };

    $scope.remove = function(id) {
        BankPatternSvc.remove({id: (id ? id : $scope.pattern.uuid)}).success(function(data) {
            $scope.list();
        });
    };

    $scope.list().then(function() {
        angular.forEach($scope.patterns, function(pattern) {
            if(pattern.uuid===$stateParams.id) {
                $scope.pattern = pattern;
            }
        });
    });
}).controller('BankPatternListCtrl', function ($scope, $alert, BankPatternSvc) {
    $scope.loading = true;

    $scope.list = function() {
        return BankPatternSvc.list().success(function(data) {
            $scope.patterns = data;
            $scope.loading = false;
        });
    };

    $scope.remove = function(id) {
        $scope.loading = true;
        BankPatternSvc.remove({id: id}).success(function(data) {
            $scope.list().then(function() {
                $alert({content: 'Pattern deleted: ' + $scope.pattern.name, placement: 'top', type: 'success', show: true, duration: 3});
            });
        });
    };

    $scope.list();
});
