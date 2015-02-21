(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.controller');
    } catch (e) {
        module = angular.module('hermes.ui.controller', []);
    }

    module.controller('BankCtrl', function ($scope, $alert, $modal, $log, $interval, FormSvc, BankSvc) {
        $scope.busy = false;
        $scope.loading = true;
        $scope.params = {};
        $scope.advanced = false;
        $scope.formName = 'bank';
        $scope.currentBankStatement = null;
        $scope.search = {};

        $scope.selectStep = function(step) {
            $scope.step = step;
            $scope.stepTemplate = 'views/bank/' + step + '/detail.html';

            $scope.busy = true;
            $scope.loading = true;

            $scope.bankStatements = null;

            var queryFn;

            if(step==='step1') {
                queryFn = BankSvc.listMatched;
            } else {
                queryFn = BankSvc.listUnmatched;
            }

            queryFn().success(function(data) {
                $scope.bankStatements = data;
                if($scope.bankStatements.length>0) {
                    $scope.edit(0);
                    if($scope.step==='step2') {
                        $scope.filter();
                    } else {
                        angular.forEach($scope.bankStatements, function(bs) {
                            if(bs.matching > 0.8) {
                                bs._selected = true;
                            }
                        });
                    }
                }
                $scope.busy = false;
                $scope.loading = false;
            }).error(function(data) {
                $scope.busy = false;
                $scope.loading = false;
                $alert({content: 'Query failed! Check input parameters.', placement: 'top', type: 'danger', show: true, duration: 5});
            });
        };

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
                $scope.bankStatements = data;
                if($scope.bankStatements.length>0) {
                    $scope.edit(0);
                    if($scope.step==='step1') {
                        $scope.filter();
                    }
                }
                $scope.busy = false;
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
            for(var i=0; i<$scope.orders.length; i++) {
                $scope.orders[i]._selected = false;
            }

            entry._selected = true;

            $scope.currentBankStatement.orderId = entry.orderId;
            $scope.currentBankStatement.firstname = entry.firstname;
            $scope.currentBankStatement.lastname = entry.lastname;
            $scope.currentBankStatement.ebayName = entry.ebayName;
            $scope.currentBankStatement.amountOrder = entry.amount;
            $scope.currentBankStatement.invoiceId = entry.invoiceId;
            $scope.currentBankStatement.customerId = entry.customerId;
            $scope.currentBankStatement._modified = true;
        };

        $scope.edit = function(index) {
            $scope.orders = null;
            $scope.currentBankStatementIndex = index;
            $scope.currentBankStatement = $scope.bankStatements[$scope.currentBankStatementIndex];
        };

        $scope.next = function() {
            $scope.select(false);
            $scope.orders = null;
            $scope.currentBankStatementIndex++;
            if($scope.currentBankStatementIndex>=$scope.bankStatements.length) {
                $scope.currentBankStatementIndex = 0;
            }
            $scope.currentBankStatement = $scope.bankStatements[$scope.currentBankStatementIndex];
            $scope.currentBankStatement._selected = true;
            $scope.search = {};
            $scope.filter();
        };

        $scope.previous = function() {
            $scope.select(false);
            $scope.orders = null;
            $scope.currentBankStatementIndex--;
            if($scope.currentBankStatementIndex<0) {
                $scope.currentBankStatementIndex = $scope.bankStatements.length-1;
            }
            $scope.currentBankStatement = $scope.bankStatements[$scope.currentBankStatementIndex];
            $scope.currentBankStatement._selected = true;
            $scope.search = {};
            $scope.filter();
        };

        $scope.save = function() {
            if($scope.currentBankStatement) {
                BankSvc.save($scope.currentBankStatement);
            }
        };

        $scope.process = function(status) {
            $scope.busy = true;

            var bankStatements = [];

            for(var i=0; i<$scope.bankStatements.length; i++) {
                if($scope.bankStatements[i]._selected) {
                    $scope.bankStatements[i].status = status;
                    $scope.bankStatements[i]._modified = undefined;
                    bankStatements.push($scope.bankStatements[i]);
                }
            }

            BankSvc.process(bankStatements).success(function(data) {
                $scope.busy = false;
                $alert({content: 'Bank statement are being processed.', placement: 'top', type: 'success', show: true, duration: 5});
                $scope.processStatusCheck();

                if($scope.step==='step2') {
                    $scope.next();
                }
            }).error(function(data) {
                $scope.busy = false;
                $alert({content: 'Bank statement processing error.', placement: 'top', type: 'danger', show: true, duration: 5});
            });
        };

        $scope.processStatusCheck = function() {
            if($scope.processStatusJob) {
                $interval.cancel($scope.processStatusJob);
            }
            $scope.processStatus();
            $scope.processStatusJob = $interval(function() {
                $scope.processStatus();
            }, 2000);
        };

        $scope.processStatus = function() {
            BankSvc.processStatus().success(function(data) {
                $scope.processRunning = Boolean(data);
                $log.info(data);
                if($scope.processRunning===false && $scope.processStatusJob) {
                    $interval.cancel($scope.processStatusJob);
                }
            }).error(function(data) {
            });
        };

        $scope.processCancel = function() {
            BankSvc.processCancel().success(function(data) {
                $scope.busy = false;
                $alert({content: 'Bank statement processing cancelled.', placement: 'top', type: 'success', show: true, duration: 5});
            }).error(function(data) {
                $scope.busy = false;
                $alert({content: 'Bank statement processing cancellation failed.', placement: 'top', type: 'danger', show: true, duration: 5});
            });
        };

        $scope.filter = function() {
            $scope.busy = true;
            var params = angular.copy($scope.search);
            params.uuid = $scope.currentBankStatement.uuid;
            if(params.orderId) {
                params.orderId = $scope.currentBankStatement.orderId;
            }
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
        $scope.selectStep('step1');
    });
})();
