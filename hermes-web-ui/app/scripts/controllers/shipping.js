/*global moment */

'use strict';

angular.module('hermes.ui').controller('ShippingCtrl', function ($scope, $log, $alert, $interval, ngAudio, ConfigurationSvc, FormSvc, ShippingSvc) {
    $scope.debugging = false;
    $scope.busy = false;
    $scope.params = {};
    $scope.checks = {};
    $scope.runState = 'stopped';
    $scope.statuses = {};
    $scope.configuration = {};
    $scope.currentLog = {};
    $scope.logging = true;
    $scope.logs = [];
    $scope.loading = true;

    $scope.successSound = ngAudio.load("audio/success.mp3");
    $scope.errorSound = ngAudio.load("audio/error.mp3");

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
    };

    $scope.selectLog = function(entry) {
        if($scope.currentLog && $scope.currentLog.orderId===entry.orderId) {
            $scope.currentLog = undefined;
        } else {
            $scope.currentLog=entry;
        }
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
            $scope.checks[shipping.id].phone = (!shipping.phone || (shipping.phone.length > 0 && shipping.phone.length <= 30) );
            $scope.checks[shipping.id].weight = (!shipping.weight || shipping.weight <= 25);
            $scope.checks[shipping.id].lastname = (!shipping.lastname || shipping.lastname.length <= 30);
            $scope.checks[shipping.id].street1 = (!shipping.street1 || (shipping.street1.length <= 30 && shipping.street1.match(/\d+/g)) );
            $scope.checks[shipping.id].street2 = (!shipping.street2 || shipping.street2.length <= 30);
            $scope.checks[shipping.id].city = (!shipping.city || shipping.city.length <= 30);

            switch(shipping.country) {
                case 'DE':
                case 'IT':
                    $scope.checks[shipping.id]['zip'] = (shipping['zip'] && (''+shipping['zip']).length === 5);
                    break;
                case 'AT':
                case 'BE':
                case 'CH':
                case 'DK':
                    $scope.checks[shipping.id]['zip'] = (shipping['zip'] && (''+shipping['zip']).length === 4);
                    break;
                default:
                    $scope.checks[shipping.id]['zip'] = true;
                    break;
            }

            if(shipping.street1 && shipping.street1.indexOf('5pack')>=0) {
                $scope.checks[shipping.id].dhlAccount = (!shipping.dhlAccount && (''+shipping.dhlAccount).length >= 5);
            } else if(shipping.street1 && shipping.street1.indexOf('packstat')>=0) {
                $scope.checks[shipping.id].dhlAccount = (!shipping.dhlAccount && (''+shipping.dhlAccount).length >= 5);
                $scope.checks[shipping.id].street2 = (!shipping.street2 || shipping.street2.length === 0);
            } else {
                $scope.checks[shipping.id].dhlAccount = true;
            }

            shipping._selected = ($scope.checks[shipping.id].company &&
                $scope.checks[shipping.id].firstname &&
                $scope.checks[shipping.id].lastname &&
                $scope.checks[shipping.id].street1 &&
                $scope.checks[shipping.id].street2 &&
                $scope.checks[shipping.id].city &&
                $scope.checks[shipping.id]['zip'] &&
                $scope.checks[shipping.id].dhlAccount);
        });
    };

    $scope.createShipmentAndLabel = function(i) {
        $scope.cancelSound();

        if ($scope.shippings && i < $scope.shippings.length) {
            var entry = $scope.shippings[i];

            if ($scope.runState === 'playing') {
                if(entry._selected) {
                    $log.debug('Processing order ID: ' + entry.orderId);
                    ShippingSvc.shipment({orderId: entry.orderId}).success(function(shipmentData) {
                        entry._updatedAt = moment();
                        entry.shipmentId = shipmentData.shipmentId;

                        ShippingSvc.label({orderId: entry.orderId}).success(function(labelData) {
                            entry._selected = false;

                            var proceed = false;

                            // store all messages
                            for(var j=0; j<labelData.length; j++) {
                                labelData[j].createdAt = moment();
                                $scope.logs.unshift(labelData[j]);

                                // proceed only if you find acceptable status
                                if(labelData[j].status==='success' || labelData[j].status==='warning') {
                                    proceed = true;
                                }
                            }

                            if(proceed) {
                                i++;
                                $scope.createShipmentAndLabel(i);
                            } else {
                                $scope.runState = 'paused';
                                $alert({content: 'Label for order ID: ' + entry.orderId + ' was not successful. Please check! Processing paused.', placement: 'top', type: 'danger', show: true});
                                $scope.loopErrorSound();
                            }
                        }).error(function(labelData) {
                            $scope.runState = 'paused';
                            $alert({content: 'Label for order ID: ' + entry.orderId + ' has an error. Please check! Processing paused.', placement: 'top', type: 'danger', show: true});
                            $scope.loopErrorSound();
                        });
                    }).error(function(shipmentData) {
                        $scope.runState = 'paused';
                        $alert({content: 'Shipment for order ID: ' + entry.orderId + ' has an error. Please check! Processing paused.', placement: 'top', type: 'danger', show: true});
                        $scope.loopErrorSound();
                    });
                } else {
                    $log.debug('Skipping order ID (not selected): ' + entry.orderId);
                    i++;
                    $scope.createShipmentAndLabel(i);
                }
            } else {
                // NOTE: pausing/stopping in the middle
                $alert({content: 'Shipment processing ' + $scope.runState + ' at #' + i + '.', placement: 'top', type: 'info', show: true, duration: 5});
            }
        } else {
            $scope.runState = 'stopped';
            $alert({content: 'All selected shipments processed (#' + i + ').', placement: 'top', type: 'success', show: true, duration: 5});
            $scope.loopSuccessSound();
        }
    };

    $scope.loopErrorSound = function() {
        // NOTE: play immediately the first time
        $scope.errorSound.play();
        $scope.errorSoundLoop = $interval(function() {
            $scope.errorSound.play();
        }, 60000); // TODO: configurable?
    };

    $scope.cancelErrorSound = function() {
        if(angular.isDefined($scope.errorSoundLoop)) {
            $interval.cancel($scope.errorSoundLoop);
            $scope.errorSoundLoop = undefined;
        }
    };

    $scope.loopSuccessSound = function() {
        // NOTE: play immediately the first time
        $scope.successSound.play();
        $scope.successSoundLoop = $interval(function() {
            $scope.successSound.play();
        }, 60000); // TODO: configurable?
    };

    $scope.cancelSuccessSound = function() {
        if(angular.isDefined($scope.successSoundLoop)) {
            $interval.cancel($scope.successSoundLoop);
            $scope.successSoundLoop = undefined;
        }
    };

    $scope.cancelSound = function() {
        $scope.cancelSuccessSound();
        $scope.cancelErrorSound();
    };

    $scope.runStateToggle = function() {
        switch($scope.runState) {
            case 'paused':
            case 'stopped':
                $scope.runState = 'playing';
                $scope.createShipmentAndLabel(0);
                break;
            case 'playing':
                $scope.runState = 'paused';
                break;
        }
    };

    $scope.runStateStop = function() {
        $scope.runState = 'stopped';
        //$scope.shippings = [];
        //$alert({content: 'Not yet activated.', placement: 'top', type: 'warning', show: true, duration: 5});
    };

    $scope.createShipment = function(entry) {
        return ShippingSvc.shipment({orderId: entry.orderId}).success(function(data) {
            entry._updatedAt = moment();
            entry.shipmentId = data.shipmentId;
        });
    };

    $scope.createLabel = function(entry) {
        return ShippingSvc.label({orderId: entry.orderId}).success(function(data) {
            // store all messages
            for(var j=0; j<labelData.length; j++) {
                data[j].createdAt = moment();
                $scope.logs.unshift(data[j]);
            }
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
        return ($scope.statuses[shipping.orderId] && $scope.statuses[shipping.orderId].length > pos);
    };

    $scope.hasStatus = function(shipping, pos) {
        return ($scope.statuses[shipping.orderId] && $scope.statuses[shipping.orderId][pos]);
    };

    $scope.statusClass = function(shipping, pos) {
        if($scope.hasStatus(shipping, pos)) {
            var status = $scope.statuses[shipping.orderId][pos];

            switch(status) {
                case 'error':
                    return 'danger';
                default:
                    return status;
            }
        }

        return 'default';
    };

    ConfigurationSvc.list().success(function(data) {
        $scope.configuration = data.properties;

        $scope.getForm($scope.configuration['hermes.shipping.form']);
    });
});
