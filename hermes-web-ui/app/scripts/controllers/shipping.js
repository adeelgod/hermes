'use strict';

angular.module('hermes.ui').controller('ShippingCtrl', function ($scope, $log, $alert, ConfigurationSvc, FormSvc, ShippingSvc) {
    $scope.debugging = false;
    $scope.busy = false;
    $scope.params = {};
    $scope.checks = {};
    $scope.statuses = {};
    $scope.configuration = {};

    $scope.debug = function() {
        $scope.debugging = !$scope.debugging;
        if($scope.debugging) {
            $scope.params.status = '%';
        }
    };

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
            $scope.check();
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
            $scope.checks[shipping.id].company = (!shipping.company || shipping.company.length <= 30);
            $scope.checks[shipping.id].firstname = (!shipping.firstname || shipping.firstname.length <= 30);
            $scope.checks[shipping.id].lastname = (!shipping.lastname || shipping.lastname.length <= 30);
            $scope.checks[shipping.id].street1 = (!shipping.street1 || shipping.street1.length <= 30);
            $scope.checks[shipping.id].street1 = (!shipping.street1 || shipping.street1.match(/\d+/g));
            $scope.checks[shipping.id].street2 = (!shipping.street2 || shipping.street2.length <= 30);
            $scope.checks[shipping.id].city = (!shipping.city || shipping.city.length <= 30);
            $scope.checks[shipping.id].zip = (shipping['zip'] && shipping['zip'].length === 5 && shipping.country==='DE');
            $scope.checks[shipping.id].dhlAccount = (shipping.dhlAccount && (''+shipping.dhlAccount).length >= 5); // TODO: check for "5pack%"

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

    $scope.status = function(shipping) {
        ShippingSvc.status({orderId: shipping.orderId}).success(function(data) {
            $scope.statuses[shipping.orderId] = data;
        });
    };

    $scope.showStatus = function(shipping, pos) {
        return ($scope.statuses[shipping.orderId] && $scope.statuses[shipping.orderId].length >= pos);
    };

    $scope.hasStatus = function(shipping, pos) {
        return ($scope.statuses[shipping.orderId] && $scope.statuses[shipping.orderId][pos]);
    };

    $scope.statusClass = function(shipping, pos) {
        if($scope.hasStatus(shipping, pos)) {
            var status = $scope.statuses[shipping.orderId][pos].status;

            status = status==='error' ? 'danger' : status;

            return status;
        }

        return 'default';
    };

    ConfigurationSvc.list().success(function(data) {
        $scope.configuration = data.properties;

        $scope.getForm($scope.configuration['hermes.shipping.form']);
    });
});
