'use strict';

angular.module('hermes.ui').controller('PrinterCtrl', function ($scope, PrinterSvc) {
    $scope.getCategoryName = function(name) {
        switch (name) {
            case 'javax.print.attribute.standard.Media':
                return 'Media';
            case 'javax.print.attribute.standard.MediaTray':
                return 'Tray';
            case 'javax.print.attribute.standard.OrientationRequested':
                return 'Orientation';
            default:
                return name;
        }
    };
    $scope.getCategoryIcon = function(name) {
        switch (name) {
            case 'javax.print.attribute.standard.Media':
                return 'fa-file-text-o';
            case 'javax.print.attribute.standard.MediaTray':
                return 'fa-inbox';
            case 'javax.print.attribute.standard.OrientationRequested':
                return 'fa-compass';
            default:
                return 'fa-question';
        }
    };
    PrinterSvc.printers().success(function(data) {
        $scope.printers = data;
    });
});
