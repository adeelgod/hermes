'use strict';

angular.module('hermes.ui').controller('ShippingCtrl', function ($scope, $log, $alert, ConfigurationSvc, FormSvc) {
    $scope.busy = false;

    $scope.getForm = function(name) {
        FormSvc.get(name).success(function(data) {
            $scope.frm = data;
            angular.forEach($scope.frm.fields, function(field) {
                if(field) {
                    var val = field.type==='BOOLEAN' ? (field.defaultValue==='true') : field.defaultValue;
                    $scope.params[field.name] = val;
                }
            });
        });
    };

    $scope.query = function() {
        $scope.params['_form'] = $scope.configuration['hermes.shipping.form'];
        $scope.busy = true;
        $scope.shippings = null;
        FormSvc.query($scope.params).success(function(data) {
            $scope.busy = false;
            $scope.shippings = data;
        }).error(function(data) {
            $scope.busy = false;
            $alert({content: 'Query failed! Check input parameters.', placement: 'top', type: 'danger', show: true, duration: 5});
        });
    };

    $scope.select = function(selected) {
        angular.forEach($scope.shippings, function(shipping) {
            shipping._selected = selected;
        });
    };

    $scope.check = function() {

    };

    ConfigurationSvc.list().success(function(data) {
        $scope.configuration = data.properties;

        $scope.getForm($scope.configuration['hermes.shipping.form'])
    });
});
