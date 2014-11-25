'use strict';

angular.module('hermes.ui').controller('FormCtrl', function ($scope, $stateParams, $alert, FormSvc) {
    $scope.forms = [];
    $scope.field = {};

    $scope.databases = [
        {'name': 'auswertung'},
        {'name': 'lcarb'}
    ];

    $scope.types = [
        {'name': 'Date', 'code': 'DATE'},
        {'name': 'Text', 'code': 'TEXT'},
        {'name': 'Boolean', 'code': 'BOOLEAN'}
    ];

    $scope.list = function() {
        return FormSvc.list().success(function(data) {
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

    $scope.reset = function() {
        $scope.form = {};
        $scope.field = {};
    };

    $scope.resetField = function() {
        $scope.field = {};
    };

    $scope.save = function() {
        FormSvc.save($scope.form).success(function(data) {
            $scope.form = data;
            $alert({content: 'Form saved: ' + $scope.form.name, placement: 'top', type: 'success', show: true, duration: 3});
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
        var id = angular.copy($scope.form.fields[index].id);
        if(id) {
            FormSvc.removeField({id: id}).success(function(data) {
                $alert({content: 'Field deleted: ' + index, placement: 'top', type: 'success', show: true, duration: 3});
                $scope.list();
            }).error(function(data) {
                $alert({content: 'Error while deleting file: ' + index, placement: 'top', type: 'danger', show: true, duration: 3});
            });
        }
        // else?!?
        $scope.form.fields.splice(index, 1);
    };

    $scope.select = function(field) {
        $scope.field = field;
    };

    $scope.list().then(function() {
        angular.forEach($scope.forms, function(form) {
            if(form.id===$stateParams.id) {
                $scope.form = form;
            }
        });
    });
}).controller('FormListCtrl', function ($scope, $alert, FormSvc) {
    $scope.list = function() {
        return FormSvc.list().success(function(data) {
            $scope.forms = data;
        });
    };

    $scope.list();
}).controller('FormExecuteCtrl', function ($scope, $stateParams, $alert, FormSvc) {
    $scope.executing = false;

    $scope.execute = function() {
        // TODO: implement this
        $scope.params['_form'] = $scope.form.name;
        $scope.executing = true;
        FormSvc.query($scope.params).success(function(data) {
            $scope.executing = false;
            $scope.result = data;
        }).error(function(data) {
            $scope.executing = false;
            $alert({content: 'Execution failed! Check input parameters.', placement: 'top', type: 'danger', show: true, duration: 5});
        });
    };

    $scope.list = function() {
        return FormSvc.list().success(function(data) {
            $scope.forms = data;
        });
    };

    $scope.list().then(function() {
        angular.forEach($scope.forms, function(form) {
            if(form.id===$stateParams.id) {
                $scope.form = form;
                $scope.params = {};
                angular.forEach(form.fields, function(field) {
                    if(field) {
                        var val = field.type==='BOOLEAN' ? (field.defaultValue==='true') : field.defaultValue;
                        $scope.params[field.name] = val;
                    }
                });
            }
        });
    });
});
