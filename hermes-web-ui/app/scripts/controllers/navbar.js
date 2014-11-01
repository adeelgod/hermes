'use strict';

angular.module('hermes.ui').controller('NavbarCtrl', function ($scope, ExampleSvc) {
    $scope.exampleQueue = function() {
        ExampleSvc.queue();
    };
});
