'use strict';

angular.module('hermes.ui').controller('ConfigurationCtrl', function ($scope, ConfigurationSvc) {
    ConfigurationSvc.list().success(function(data) {
        $scope.configuration = data;
    });
});
