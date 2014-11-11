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
            },
            print: function(params) {
                return $http({
                    method: 'POST',
                    url: 'api/printers/print',
                    data: params
                });
            }
        };
    });
