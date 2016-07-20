(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.controller');
    } catch (e) {
        module = angular.module('hermes.ui.controller', []);
    }

    module.controller('DocumentsCtrl', function ($scope, $interval, $log, $alert, $modal, ConfigurationSvc, FormSvc, DocumentsSvc) {
        $scope.params = {_order_ids: []};

        $scope.busy = false;

        $scope.loading = true;
        
        $scope.printjobId = null;

        $scope.currentOrder = {};

        $scope.currentOrder = {};
        
        $scope.substate = 'new';

        var confirmModal = $modal({title: 'Order print files missing', content: 'Are you sure you want to proceed?', scope: $scope, template: 'parts/confirm.html', show: false, placement: 'center'});
        confirmModal.$scope.confirm = function() {
            confirmModal.hide();
            $scope.create();
        };
        confirmModal.$scope.close = function() {
            confirmModal.hide();
        };

        $scope.checkBeforeCreate = function() {
        	$scope.substate = 'create';
        	// TODO remove
        	//$scope.create();
        	//return;
        	
            var execute = true;

            for(var i=0; i<$scope.orders.length; i++) {
                var order = $scope.orders[i];
                if(order._selected && (!order._invoiceExists || !order._labelExists)) {
                    confirmModal.$promise.then(confirmModal.show);
                    execute = false;
                    break;
                }
            }

            if(execute) {
                confirmModal.$scope.confirm();
            }
        };
        
        $scope.getForm = function(name) {
            FormSvc.get(name).success(function(data) {
                $scope.frm = data;
                angular.forEach($scope.frm.fields, function(field) {
                    if(field && field.parameter) {
                        var val = field.fieldType==='BOOLEAN' ? (field.defValue==='true') : field.defValue;
                        $scope.params[field.name] = val;
                        
                        if(field.name==='from') {
                            $scope.params['from'] = moment().startOf('day');
                        } else if(field.name==='until') {
                            $scope.params['until'] = moment().startOf('day').add(24, 'hours');
                        }
                    }
                });
                $scope.loading = false;
            });
        };

        $scope.charge = function(index) {
            var chargeSize = Number($scope.configuration['hermes.charge.size']);
            return Math.ceil( (index+1)/chargeSize);
        };

        $scope.query = function() {
        	$scope.substate = 'new';
            $scope.params['_form'] = 'printjob_new_date';
            $scope.params['_checkFiles'] = true;
            $scope.params['_downloadFiles'] = true;
            $scope.busy = true;
            $scope.orders = null;
        	$scope.printjobs = null;
        	$scope.printjobItems = null;
        	$scope.printjobId = null;
            FormSvc.query($scope.params).success(function(data) {
                $scope.busy = false;
                $scope.orders = data;
                angular.forEach($scope.orders, function(order) {
                	order._selected = true;
                });
            }).error(function(data) {
                $scope.busy = false;
                $alert({content: 'Query failed! Check input parameters.', placement: 'top', type: 'danger', show: true, duration: 5});
            });
        };
        
        $scope.listPrintjobs = function() {
        	$scope.substate = 'listPrintjobs';
        	// TODO to config
        	$scope.params['_form'] = 'printjobs';
            $scope.params['_checkFiles'] = false;
            $scope.params['_downloadFiles'] = false;
        	$scope.busy = true;
            $scope.orders = null;
        	$scope.printjobs = null;
        	$scope.printjobItems = null;
        	$scope.printjobId = null;
        	FormSvc.query($scope.params).success(function(data) {
                $scope.busy = false;
                $scope.printjobs = data;
        	}).error(function(data) {
        		$scope.busy = false;
        		$alert({content: 'Query failed! Check input parameters.', placement: 'top', type: 'danger', show: true, duration: 5});
        	});
        }
        
        $scope.listPrintjobItems = function(printjobId) {
        	// TODO to config
        	$scope.params['_form'] = 'printjob_items';
            $scope.params['_checkFiles'] = false;
            $scope.params['_downloadFiles'] = false;
        	$scope.params['printjobId'] = printjobId;
        	$scope.busy = true;
            $scope.orders = null;
        	$scope.printjobs = null;
        	$scope.printjobItems = null;
        	$scope.printjobId = printjobId;
        	FormSvc.query($scope.params).success(function(data) {
        		$scope.busy = false;
        		$scope.printjobItems = data;
                angular.forEach($scope.printjobItems, function(p) {
                	if (p.status != "printed") {
                		p._selected = true;
                	}
                });
        	}).error(function(data) {
        		$scope.busy = false;
        		$alert({content: 'Query failed! Check input parameters.', placement: 'top', type: 'danger', show: true, duration: 5});
        	});
        }
        

        $scope.select = function(selected) {
            angular.forEach($scope.orders, function(order) {
                order._selected = selected;
            });
        };

        $scope.selectPrintjobItems = function(selected) {
        	angular.forEach($scope.printjobItems, function(p) {
        		p._selected = selected;
        	});
        };
        
        $scope.selectEntry = function(entry) {
            entry._selected=!entry._selected;
        };
        
        $scope.printCancel = function() {
            PrinterSvc.cancel().success(function(data) {
                $scope.busy = false;
                if($scope.printStatusLoop) {
                    $interval.cancel($scope.printStatusLoop);
                    $scope.printStatusLoop = undefined;
                }
                $alert({content: 'Printing cancelled!', placement: 'top', type: 'danger', show: true, duration: 5});
            }).error(function(data) {
                // TODO: not sure about that
                //$scope.busy = false;
                //$alert({content: 'Error while printing documents', placement: 'top', type: 'danger', show: true, duration: 5});
            });
        };
        
        $scope.create = function() {
            var req = {orderIds: []};

            for(var i=0; i<$scope.orders.length; i++) {
                if($scope.orders[i] && $scope.orders[i]._selected) {
                	req.orderIds.push($scope.orders[i].orderId);
                }
            }
            
        	$scope.busy = true;
        	
            DocumentsSvc.create(req).success(function(data) {
            	$scope.busy = false;
            	$scope.listPrintjobs();
            	$alert({content: 'Printjob created', placement: 'top', type: 'success', show: true, duration: 5});
        	}).error(function(data) {
                $scope.busy = false;
                $alert({content: 'Error while creating printjob', placement: 'top', type: 'danger', show: true, duration: 5});
        	});
        	
        }
        
        $scope.print = function() {
        	$scope.substate = 'print';
        	if ($scope.printjobItems == null) {
                $alert({content: 'Please select a printjob first', placement: 'top', type: 'danger', show: true, duration: 5});
                return;
        	}
        	$scope.busy = true;
        	
            var req = {printjobId: $scope.printjobId, printjobItems: []};
            
            for(var i=0; i<$scope.printjobItems.length; i++) {
                if($scope.printjobItems[i] && $scope.printjobItems[i]._selected) {
                	req.printjobItems.push($scope.printjobItems[i].id);
                }
            }
        	
            DocumentsSvc.printJob(req).success(function(data) {
            	$scope.busy = false;
            	$scope.listPrintjobItems($scope.printjobId);
            	$alert({content: 'Printjob completed', placement: 'top', type: 'success', show: true, duration: 5});
        	}).error(function(data) {
                $scope.busy = false;
                $alert({content: 'Error while printing documents', placement: 'top', type: 'danger', show: true, duration: 5});
        	});

        }
        
        $scope.print_old = function() {
            // TODO: remove
            iterator = -1;
            count = 0;

            $scope.busy = true;

            // new approach
            var req = {charges: [], chargeSize: $scope.configuration['hermes.charge.size']};

            var prevCharge = 0;
            var charge = {};

            for(var i=0; i<$scope.orders.length; i++) {
                var curCharge = $scope.charge(i);
                if(curCharge!==prevCharge) {
                    charge = {};
                    charge.pos = curCharge;
                    charge.orders = [];
                    req.charges.push(charge);
                    prevCharge = curCharge;
                }
                if($scope.orders[i] && $scope.orders[i]._selected) {
                	charge.orders.push($scope.orders[i].orderId);
                }
            }

            PrinterSvc.printAll(req).success(function(data) {
                //$scope.busy = false;
                $alert({content: 'Documents queued', placement: 'top', type: 'success', show: true, duration: 5});
                $scope.printStatusLoop = $interval(function() {
                    PrinterSvc.status().success(function(data) {
                        $scope.busy = Boolean(data);
                        if(!$scope.busy) {
                            $interval.cancel($scope.printStatusLoop);
                            $scope.printStatusLoop = undefined;
                            $alert({content: 'Print queue empty.', placement: 'top', type: 'success', show: true});
                        }
                    }).error(function(data) {
                        // TODO: not sure about that
                        //$scope.busy = false;
                        //$alert({content: 'Error while printing documents', placement: 'top', type: 'danger', show: true, duration: 5});
                    });
                }, 5000); // TODO: configurable?
            }).error(function(data) {
                $scope.busy = false;
                $alert({content: 'Error while printing documents', placement: 'top', type: 'danger', show: true, duration: 5});
            });
            //printNext();
        };

        ConfigurationSvc.list().success(function(data) {
            $scope.loading = true;

            $scope.configuration = data.properties;

            //$scope.getForm($scope.configuration['hermes.orders.form']);
            $scope.getForm('printjob_new_date');
        });

        //FormSvc.synchronize($scope);

        //$alert({content: 'Hi Daniel. Die Navigationsleiste ist jetzt umgestaltet. Melde Dich als Admin an, um alle Buttons zu sehen.', placement: 'top', type: 'info', show: true});
    });
})();
