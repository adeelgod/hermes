'use strict';

angular.module('hermes.ui').controller('ShippingCtrl', function ($scope, $log, $alert, ConfigurationSvc, FormSvc, ShippingSvc) {
    $scope.debugging = false;
    $scope.busy = false;
    $scope.params = {};
    $scope.checks = {};
    $scope.statuses = {};
    $scope.configuration = {};
    $scope.currentLog = {};
    $scope.logging = true;
    $scope.logs = [
        {
            orderId: '300014222',
            status: 'error',
            message: 'DHL Intraship::create:: %::at least one shipment has errors %'
        },
        {
            orderId: '300014287',
            status: 'info',
            message: 'DHL Intraship::create::[%]::ok | warning: the address could not be validated'
        },
        {
            orderId: '300000011',
            status: 'success',
            message: 'DHL Intraship::pdf::0::PDF creation was successful'
        },
        {
            orderId: '300014284',
            status: 'error',
            message: 'DHL Intraship::create:: %::Invalid fieldlength in %'
        },
        {
            orderId: '300014288',
            status: 'warning',
            message: 'DHL Intraship::create::[%]::Unable to save PDF'
        },
        {
            orderId: '300014285',
            status: 'error',
            message: 'DHL Intraship::create:: %::Invalid value %'
        },
        {
            orderId: '300014289',
            status: 'warning',
            message: 'DHL Intraship::create::[%]::Could not connect to host'
        },
        {
            orderId: '300014286',
            status: 'error',
            message: 'DHL Intraship::create:: %::at least one shipment has errors %'
        },
        {
            orderId: '300000010',
            status: 'success',
            message: 'DHL Intraship::pdf::0::PDF creation was successful'
        },
        {
            orderId: '300000008',
            status: 'warning',
            message: 'DHL Intraship::create::[%]::Not Found'
        },
        {
            orderId: '300000009',
            status: 'warning',
            message: 'DHL Intraship::create::[%]::Login failed'
        },
        {
            orderId: '300000012',
            status: 'success',
            message: 'DHL Intraship::pdf::0::PDF creation was successful'
        },
        {
            orderId: '300000013',
            status: 'success',
            message: 'DHL Intraship::pdf::0::PDF creation was successful'
        }
    ]; // TODO: should be empty
    $scope.loading = true;

    $scope.debug = function() {
        $scope.debugging = !$scope.debugging;
        if($scope.debugging) {
            $scope.params.status = '%';
        }
    };

    $scope.createShipping = function(entry) {
        $alert({content: 'Create shipping not yet activated.', placement: 'top', type: 'warning', show: true, duration: 5});
    };

    $scope.createLabel = function(entry) {
        $alert({content: 'Create label not yet activated.', placement: 'top', type: 'warning', show: true, duration: 5});
    };

    $scope.create = function() {
        $alert({content: 'Not yet activated.', placement: 'top', type: 'warning', show: true, duration: 5});
    };

    $scope.selectLog = function(entry) {
        $scope.currentLog=entry;
    };

    $scope.clearLogs = function() {
        $scope.logs = [];
    };

    $scope.getForm = function(name) {
        $scope.loading = true;
        FormSvc.get(name).success(function(data) {
            $scope.frm = data;
            angular.forEach($scope.frm.fields, function(field) {
                if(field && field.parameter) {
                    var val = field.fieldType==='BOOLEAN' ? (field.defValue==='true') : field.defValue;
                    $scope.params[field.name] = val;
                }
            });
            $scope.loading = false;
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

            if(shipping.country==='DE') {
                $scope.checks[shipping.id].zip = (shipping['zip'] && (''+shipping['zip']).length === 5);
            } else {
                $scope.checks[shipping.id].zip = true;
            }

            if(shipping.street1 && shipping.street1.indexOf('5pack')>=0) {
                $scope.checks[shipping.id].dhlAccount = (!shipping.dhlAccount && (''+shipping.dhlAccount).length >= 5);
            } else {
                $scope.checks[shipping.id].dhlAccount = true;
            }

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

    $scope.statusAll = function() {
        angular.forEach($scope.shippings, function(shipping) {
            $scope.status(shipping);
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
