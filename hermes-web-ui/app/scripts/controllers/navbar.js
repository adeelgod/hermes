'use strict';

angular.module('hermes.ui').controller('NavbarCtrl', function ($scope, ExampleSvc, FormSvc) {
    $scope.loading = true;
    $scope.forms = [];

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
        shipping: {title: 'Search shippings...', placement: 'bottom', type: 'info'},
        execute: {title: 'Print reports...', placement: 'bottom', type: 'info'},
        examples: {title: 'Copy example PDFs...', placement: 'bottom', type: 'info'},
        printers: {title: 'Show printers...', placement: 'bottom', type: 'info'},
        forms: {title: 'Edit forms and queries...', placement: 'bottom', type: 'info'},
        configuration: {title: 'Change configuration...', placement: 'bottom', type: 'info'},
        signin: {title: 'Login...', placement: 'bottom', type: 'info'},
        logout: {title: 'Logout...', placement: 'bottom', type: 'info'}
    };

    $scope.list = function() {
        $scope.loading = true;
        $scope.forms = [];
        return FormSvc.list().success(function(data) {
            angular.forEach(data, function(form) {
                if(form.accessPublic) {
                    $scope.forms.push({text: form.name + ' - ' + form.description, href: '#!/form/execute/' + form.id});
                }
            });
            $scope.loading = false;
        });
    };

    $scope.exampleQueue = function() {
        ExampleSvc.queue();
    };

    $scope.list();
});
