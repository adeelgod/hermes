'use strict';

angular.module('hermes.ui')
    .factory('PrinterSvc', function ($http) {
        // Public API here
        return {
            printers: function() {
                return $http({
                    method: 'GET',
                    url: 'api/printers'
                });
            }
        };
    });
