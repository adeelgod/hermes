'use strict';

angular.module('hermes.ui').controller('FormCtrl', function ($scope, FormSvc) {
    $scope.name = '';
    $scope.field = {};

    $scope.listForms = function() {
        FormSvc.listForms().success(function(data) {
            $scope.forms = data;
        });
    };

    $scope.listFields = function() {
        FormSvc.listFields($scope.name).success(function(data) {
            $scope.fields = data;
        });
    };

    $scope.save = function() {
        FormSvc.save($scope.field).success(function(data) {
            $alert({content: 'Field saved: ' + field.name, placement: 'top', type: 'success', show: true, duration: 3});
            $scope.listFields();
        });
    };

    $scope.remove = function(field) {
        FormSvc.remove({uid: field.uid}).success(function(data) {
            $alert({content: 'Field deleted: ' + field.name, placement: 'top', type: 'success', show: true, duration: 3});
            $scope.listFields();
        });
    };

    $scope.select = function(field) {
        $scope.field = field;
    };

    $scope.listForms();
});
