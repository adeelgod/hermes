(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.crm.controller');
    } catch (e) {
        module = angular.module('hermes.crm.controller', []);
    }

    module.controller('SignInCtrl', function ($scope, SecuritySvc) {
        // TODO: implement this

        $scope.login = {
            rememberMe: true
        };

        $scope.signin = function() {
            SecuritySvc.login($scope.login);
        };
    });
})();
