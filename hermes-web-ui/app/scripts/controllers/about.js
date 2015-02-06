(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.controller');
    } catch (e) {
        module = angular.module('hermes.ui.controller', []);
    }

    module.controller('AboutCtrl', function ($scope, VersionSvc) {
        VersionSvc.version().success(function(data) {
            $scope.version = data;
        });
    });
})();
