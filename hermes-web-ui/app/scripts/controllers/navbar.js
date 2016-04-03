(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.controller');
    } catch (e) {
        module = angular.module('hermes.ui.controller', []);
    }

    module.controller('NavbarCtrl', function ($scope, $interval, $alert, FormSvc, DhlSvc, ShippingSvc, UtilSvc) {
        $scope.loading = true;
        $scope.forms = [];
        $scope.updateForm = {};

        $scope.dropdown = [
            {
                'text': '<i class=\"fa fa-pencil\"></i>&nbsp;Edit',
                'href': '#!/form/edit'
            },
            {
                "text": '<i class=\"fa fa-pencil\"></i>&nbsp;List',
                "href": '#!/form/list'
            }
        ];

        $scope.tooltips = {
            help: {title: 'Help', placement: 'bottom', type: 'info'},
            orders: {title: 'Search orders...', placement: 'bottom', type: 'info'},
            documents: {title: 'Search documents...', placement: 'bottom', type: 'info'},
            shipping: {title: 'Search shippings...', placement: 'bottom', type: 'info'},
            execute: {title: 'Print reports...', placement: 'bottom', type: 'info'},
            examples: {title: 'Copy example PDFs...', placement: 'bottom', type: 'info'},
            printers: {title: 'Show printers...', placement: 'bottom', type: 'info'},
            forms: {title: 'Edit forms and queries...', placement: 'bottom', type: 'info'},
            configuration: {title: 'Change configuration...', placement: 'bottom', type: 'info'},
            signin: {title: 'Login...', placement: 'bottom', type: 'info'},
            logout: {title: 'Logout...', placement: 'bottom', type: 'info'}
        };

        $scope.flush = function() {
            ShippingSvc.flush();
        };

        $scope.netdrive = function() {
            UtilSvc.netdrive();
        };

        $scope.trackingCheck = function () {
            $scope.checkBusy = true;
            DhlSvc.trackingCheck().success(function(data) {
                $alert({
                    content: 'Checks scheduled...',
                    placement: 'top',
                    type: 'success',
                    show: true,
                    duration: 3});
                $scope.trackingCheckLoop = $interval(function() {
                    DhlSvc.trackingCheckStatus().success(function(data) {
                        $scope.checkBusy = Boolean(data);
                        if(!$scope.checkBusy) {
                            $interval.cancel($scope.trackingCheckLoop);
                            $scope.trackingCheckLoop = undefined;
                            $alert({content: 'All tracking checks finished.', placement: 'top', type: 'success', show: true, duration: 3});
                        }
                    });
                }, 5000); // TODO: configurable?
            }).error(function(data) {
                $alert({content: 'DHL Tracking Status: ' + data.message, placement: 'top', type: 'danger', show: true, duration: 15});
                $scope.checkBusy = false;
            });
        };

        $scope.trackingCheckCancel = function () {
            DhlSvc.trackingCheckCancel().success(function(data) {
                $alert({
                    content: 'Tracking check cancelled!',
                    placement: 'top',
                    type: 'success',
                    show: true,
                    duration: 3});
            });
        };

        $scope.trackingStatus = function () {
            DhlSvc.trackingStatus({code: $scope.trackingCode}).success(function(data) {
                $alert({
                    content: data.date + ': ' + data.message + (data.next ? ' (next: ' + data.next + ')':''),
                    placement: 'top',
                    type: data.status,
                    show: true,
                    duration: 15});
            }).error(function(data) {
                $alert({content: 'DHL Tracking Status: ' + data.message, placement: 'top', type: 'danger', show: true, duration: 15});
            });
        };

        $scope.list = function() {
            $scope.loading = true;
            $scope.forms = [];
            return FormSvc.list().success(function(data) {
                angular.forEach(data, function(form) {
                    if(form.accessPublic) {
                        $scope.forms.push({text: form.description, href: '#!/form/execute/' + form.id});
                    }
                    if(form.name==='update') {
                        $scope.updateForm = form;
                    }
                });
                $scope.loading = false;
            });
        };

        $scope.synchronize = function() {
            FormSvc.synchronize($scope);
            /**
             $scope.loading = true;
             var params = {};
             params['_form'] = $scope.updateForm.name;
             FormSvc.query(params).success(function(data) {
            $scope.loading = false;
            $alert({content: 'Sync success!', placement: 'top', type: 'success', show: true, duration: 5});
        }).error(function(data) {
            $scope.loading = false;
            $alert({content: 'Sync failed!', placement: 'top', type: 'danger', show: true, duration: 5});
        });
             */
        };

        $scope.$on('form.reload', function() {
            $scope.list();
        });

        $scope.list();
    });
})();
