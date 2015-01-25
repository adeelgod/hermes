(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.controller');
    } catch (e) {
        module = angular.module('hermes.ui.controller', []);
    }

    module.controller('AppCtrl', function ($scope, SecuritySvc) {
        $scope.authenticated = false;

        $scope.$on('hermes.authenticated', function (event, data) {
            $scope.authenticated = data;
        });

        $scope.logout = function() {
            SecuritySvc.logout();
        };
    });
})();
