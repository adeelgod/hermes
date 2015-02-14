(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.crm.controller');
    } catch (e) {
        module = angular.module('hermes.crm.controller', []);
    }

    module.controller('AboutCtrl', function ($scope, $alert, VersionSvc, SupportSvc) {
        $scope.support = {};

        $scope.send = function() {
            SupportSvc.send($scope.support).success(function(data) {
                $alert({content: 'Support notified.', placement: 'top', type: 'success', show: true, duration: 3});
                $scope.support = {};
            });
        };

        $scope.version = function() {
            VersionSvc.version().success(function(data) {
                $scope.version = data;
            });
        };

        $scope.version();
    });
})();
