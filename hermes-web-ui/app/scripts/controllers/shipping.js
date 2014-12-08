/*global moment */

'use strict';

angular.module('hermes.ui').controller('ShippingCtrl', function ($scope, $log, $alert, ConfigurationSvc, FormSvc, ShippingSvc) {
    $scope.debugging = false;
    $scope.busy = false;
    $scope.params = {};
    $scope.checks = {};
    $scope.runState = 'stopped';
    $scope.statuses = {};
    $scope.configuration = {};
    $scope.currentLog = {};
    $scope.logging = true;
    $scope.logs = [
        {
            orderId: '300014222',
            createdAt: moment(),
            status: 'error',
            message: 'DHL Intraship::create:: %::at least one shipment has errors %'
        },
        {
            orderId: '300014287',
            createdAt: moment().subtract(5, 'minutes'),
            status: 'info',
            message: 'DHL Intraship::create::[%]::ok | warning: the address could not be validated'
        },
        {
            orderId: '300000011',
            createdAt: moment().subtract(7, 'minutes'),
            status: 'success',
            message: 'DHL Intraship::pdf::0::PDF creation was successful'
        },
        {
            orderId: '300014284',
            createdAt: moment().subtract(8, 'minutes'),
            status: 'error',
            message: 'DHL Intraship::create:: %::Invalid fieldlength in %'
        },
        {
            orderId: '300014288',
            createdAt: moment().subtract(9, 'minutes'),
            status: 'warning',
            message: 'DHL Intraship::create::[%]::Unable to save PDF'
        },
        {
            orderId: '300014285',
            createdAt: moment().subtract(10, 'minutes'),
            status: 'error',
            message: 'DHL Intraship::create:: %::Invalid value %'
        },
        {
            orderId: '300014289',
            createdAt: moment().subtract(12, 'minutes'),
            status: 'warning',
            message: 'DHL Intraship::create::[%]::Could not connect to host'
        },
        {
            orderId: '300014286',
            createdAt: moment().subtract(14, 'minutes'),
            status: 'error',
            message: 'DHL Intraship::create:: %::at least one shipment has errors %'
        },
        {
            orderId: '300000010',
            createdAt: moment().subtract(16, 'minutes'),
            status: 'success',
            message: 'DHL Intraship::pdf::0::PDF creation was successful'
        },
        {
            orderId: '300000008',
            createdAt: moment().subtract(17, 'minutes'),
            status: 'warning',
            message: 'DHL Intraship::create::[%]::Not Found'
        },
        {
            orderId: '300000009',
            createdAt: moment().subtract(19, 'minutes'),
            status: 'warning',
            message: 'DHL Intraship::create::[%]::Login failed'
        },
        {
            orderId: '300000012',
            createdAt: moment().subtract(21, 'minutes'),
            status: 'success',
            message: 'DHL Intraship::pdf::0::PDF creation was successful'
        },
        {
            orderId: '300000013',
            createdAt: moment().subtract(23, 'minutes'),
            status: 'success',
            message: 'DHL Intraship::pdf::0::PDF creation was successful'
        }
    ]; // TODO: should be empty
    $scope.loading = true;

    $scope.tooltips = {
        debugShowHide: {title: 'Show/Hide Debug Functions', placement: 'bottom', type: 'info'},
        shipmentSearch: {title: 'Run Query', placement: 'bottom', type: 'info'},
        shipmentRunStartPause: {title: 'Start/Pause Processing', placement: 'bottom', type: 'info'},
        shipmentRunStop: {title: 'Abort', placement: 'bottom', type: 'info'},
        shipmentStatus: {title: 'Check Intraship Stati', placement: 'bottom', type: 'info'},
        shipmentCreate: {title: 'Create Shipment', placement: 'bottom', type: 'info'},
        shipmentLabel: {title: 'Create Intraship Label', placement: 'bottom', type: 'info'},
        logShowHide: {title: 'Show/Hide Log Window', placement: 'bottom', type: 'info'},
        logClear: {title: 'Clear Log', placement: 'bottom', type: 'info'},
        logEntries: {title: 'Number of Log Entries', placement: 'bottom', type: 'info'}
    };

    $scope.debug = function() {
        $scope.debugging = !$scope.debugging;
        if($scope.debugging) {
            $scope.params.status = '%';
        }
    };

    $scope.runStateToggle = function() {
        switch($scope.runState) {
            case 'paused':
            case 'stopped':
                $scope.runState = 'playing';
                break;
            case 'playing':
                $scope.runState = 'paused';
                break;
        }
    };

    $scope.runStateStop = function() {
        $scope.runState = 'stopped';
        //$alert({content: 'Not yet activated.', placement: 'top', type: 'warning', show: true, duration: 5});
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
            $scope.checks[shipping.id].weight = (!shipping.weight || shipping.weight <= 25);
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

    $scope.createShipment = function(entry) {
        ShippingSvc.shipment({orderId: entry.orderId}).success(function(data) {
            entry._updatedAt = moment();
            entry.shipmentId = data.shipmentId;
        });
    };

    $scope.createLabel = function(entry) {
        ShippingSvc.label({orderId: entry.orderId}).success(function(data) {
            data.createdAt = moment();
            $scope.logs.unshift(data);
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
