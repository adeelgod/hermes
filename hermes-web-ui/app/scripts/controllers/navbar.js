'use strict';

angular.module('hermes.ui').controller('NavbarCtrl', function ($scope, ExampleSvc) {
    $scope.dropdown = [
        {
            'text': '<i class=\"fa fa-pencil\"></i>&nbsp;Edit',
            'href': "#!/form/edit"
        },
        {
            "text": '<i class=\"fa fa-pencil\"></i>&nbsp;List',
            "href": '#!/form/list'
        }
    ];

    $scope.exampleQueue = function() {
        ExampleSvc.queue();
    };
});
