'use strict';

angular.module('hermes.ui').controller('BankCtrl', function ($scope, $alert, $modal, FormSvc, BankSvc) {
    $scope.busy = false;
    $scope.loading = true;
    $scope.params = {};
    $scope.advanced = false;
    $scope.formName = 'bank';
    $scope.currentBankStatement = null;
    $scope.search = {};

    var editModal = $modal({scope: $scope, template: 'views/bank/edit.tpl.html', show: false});

    $scope.getForm = function() {
        $scope.loading = true;
        FormSvc.get($scope.formName).success(function(data) {
            $scope.frm = data;
            $scope.params = {};
            angular.forEach($scope.frm.fields, function(field) {
                if(field && field.parameter) {
                    var val = field.fieldType==='BOOLEAN' ? (field.defValue==='true') : field.defValue;
                    $scope.params[field.name] = val;

                    if(field.name==='from') {
                        $scope.params['from'] = moment().startOf('day').subtract(1, 'year');
                    } else if(field.name==='until') {
                        $scope.params['until'] = moment().startOf('day').add(24, 'hours');
                    }
                }
            });
            $scope.loading = false;
        });
    };

    $scope.query = function() {
        $scope.params['_form'] = $scope.formName;
        $scope.busy = true;
        $scope.bankStatements = null;
        FormSvc.query($scope.params).success(function(data) {
            $scope.busy = false;
            $scope.bankStatements = data;
        }).error(function(data) {
            $scope.busy = false;
            $alert({content: 'Query failed! Check input parameters.', placement: 'top', type: 'danger', show: true, duration: 5});
        });
    };

    $scope.select = function(selected) {
        angular.forEach($scope.bankStatements, function(bankStatement) {
            bankStatement._selected = selected;
        });
    };

    $scope.selectOrder = function(entry) {
        angular.forEach($scope.orders, function(order) {
            order._selected = (entry.uuid===order.uuid);
        });
    };

    $scope.edit = function(index) {
        $scope.orders = null;
        $scope.currentBankStatementIndex = index;
        $scope.currentBankStatement = $scope.bankStatements[$scope.currentBankStatementIndex];
        editModal.$promise.then(editModal.show);
    };

    $scope.next = function() {
        $scope.orders = null;
        $scope.currentBankStatementIndex++;
        if($scope.currentBankStatementIndex>=$scope.bankStatements.length) {
            $scope.currentBankStatementIndex = 0;
        }
        $scope.currentBankStatement = $scope.bankStatements[$scope.currentBankStatementIndex];
        $scope.search = {};
    };

    $scope.previous = function() {
        $scope.orders = null;
        $scope.currentBankStatementIndex--;
        if($scope.currentBankStatementIndex<0) {
            $scope.currentBankStatementIndex = $scope.bankStatements.length-1;
        }
        $scope.currentBankStatement = $scope.bankStatements[$scope.currentBankStatementIndex];
        $scope.search = {};
    };

    $scope.assign = function() {
        // TODO: implement this
        $scope.currentBankStatement.status='confirmed';
        $alert({content: 'Not yet implemented.', placement: 'top', type: 'warning', show: true, duration: 5});
    };

    $scope.ignore = function() {
        // TODO: implement this
        $scope.currentBankStatement.status='ignored';
        $alert({content: 'Not yet implemented.', placement: 'top', type: 'warning', show: true, duration: 5});
    };

    $scope.reset = function() {
        // TODO: implement this
        $scope.currentBankStatement.status='new';
        $alert({content: 'Not yet implemented.', placement: 'top', type: 'warning', show: true, duration: 5});
    };

    $scope.filter = function() {
        $scope.busy = true;
        var params = angular.copy($scope.search);
        params.uuid = $scope.currentBankStatement.uuid;
        BankSvc.filter(params).success(function(data) {
            $scope.orders = data;
            $scope.busy = false;
        }).error(function(data) {
            $scope.busy = false;
            $alert({content: 'Could not find any matches.', placement: 'top', type: 'danger', show: true, duration: 5});
        });
    };

    $scope.toggleAdvanced = function() {
        $scope.advanced = !$scope.advanced;

        if($scope.advanced) {
            $scope.formName = 'bank_advanced';
        } else {
            $scope.formName = 'bank';
        }
        $scope.getForm();
    };

    $scope.getForm();
});
