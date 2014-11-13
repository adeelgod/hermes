'use strict';

angular.module('hermes.ui').controller('AppCtrl', function ($scope, SecuritySvc) {
    $scope.authenticated = false;

    $scope.$on('hermes.authenticated', function (event, data) {
        $scope.authenticated = data;
    });

    $scope.logout = function() {
        SecuritySvc.logout();
    };
});
