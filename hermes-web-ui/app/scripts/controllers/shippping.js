'use strict';

angular.module('hermes.ui').controller('ShippingCtrl', function ($scope, $log, $alert, ConfigurationSvc, FormSvc) {
    $scope.busy = false;
    $scope.checks = {};

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
        angular.forEach($scope.shippings, function(shipping) {
            if(!$scope.checks[shipping.id]) {
                $scope.checks[shipping.id] = {};
            }

            // TODO: make this configurable?!?
            $scope.checks[shipping.id].company = !(shipping.company.length > 30);
            $scope.checks[shipping.id].firstname = !(shipping.firstname.length > 30);
            $scope.checks[shipping.id].lastname = !(shipping.lastname.length > 30);
            $scope.checks[shipping.id].street1 = !(shipping.street1.length > 30);
            // TODO: Strasse enthält keine numerische information (Regex)
            $scope.checks[shipping.id].street2 = !(shipping.street2.length > 30);
            $scope.checks[shipping.id].city = !(shipping.city.length > 30);
            $scope.checks[shipping.id].zip = !(shipping.zip.length !== 5 && shipping.country==='DE');
            $scope.checks[shipping.id].dhlAccount = !(shipping.dhlAccount.length < 5); // TODO: check for "5pack%"

            shipping._selected = ($scope.checks[shipping.id].company &&
                $scope.checks[shipping.id].firstname &&
                $scope.checks[shipping.id].lastname &&
                $scope.checks[shipping.id].street1 &&
                $scope.checks[shipping.id].street1 &&
                $scope.checks[shipping.id].city &&
                $scope.checks[shipping.id].zip &&
                $scope.checks[shipping.id].dhlAccount);
        });
    };

    ConfigurationSvc.list().success(function(data) {
        $scope.configuration = data.properties;

        $scope.getForm($scope.configuration['hermes.shipping.form'])
    });
});
