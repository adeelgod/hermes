'use strict';

angular.module('hermes.ui').controller('FormCtrl', function ($scope, $alert, FormSvc) {
    $scope.form = {};
    $scope.forms = [];
    $scope.field = {};

    $scope.list = function() {
        FormSvc.list().success(function(data) {
            $scope.forms = data;
        });
    };

    $scope.add = function() {
        if(!$scope.form.fields) {
            $scope.form.fields = [];
        }

        $scope.field.formId = $scope.form.id;

        $scope.form.fields.push(angular.copy($scope.field));
        $scope.field = {};
    };

    $scope.save = function() {
        FormSvc.save($scope.form).success(function(data) {
            $alert({content: 'Form saved: ' + form.name, placement: 'top', type: 'success', show: true, duration: 3});
            $scope.list();
        });
    };

    $scope.removeForm = function() {
        FormSvc.removeField({id: $scope.form.id}).success(function(data) {
            $alert({content: 'Form deleted: ' + $scope.form.name, placement: 'top', type: 'success', show: true, duration: 3});
            $scope.list();
        });
    };

    $scope.removeField = function(index) {
        var id = $scope.form.fields[index].id;
        if(id) {
            FormSvc.removeField({id: id}).success(function(data) {
                $alert({content: 'Field deleted: ' + field.name, placement: 'top', type: 'success', show: true, duration: 3});
                $scope.list();
            });
        }
        // else?!?
        $scope.form.fields.splice(index, 1);
    };

    $scope.select = function(field) {
        $scope.field = field;
    };

    $scope.list();
});
