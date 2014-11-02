'use strict';

angular.module('hermes.ui')
    .factory('PrinterLogSvc', function ($http) {
        // Public API here
        return {
            list: function() {
                return $http({
                    method: 'GET',
                    url: 'api/printers/logs'
                });
            }
        };
    });
