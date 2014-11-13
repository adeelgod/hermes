'use strict';

angular.module('hermes.ui').controller('SignInCtrl', function ($scope, SecuritySvc) {
    // TODO: implement this

    $scope.login = {
        rememberMe: true
    };

    $scope.signin = function() {
        SecuritySvc.login($scope.login);
    };
});
