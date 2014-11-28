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
