'use strict';

angular.module('hermes.ui')
    .factory('PrinterLogSvc', function ($http) {
        // Public API here
        return {
            list: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/printers/logs',
                    params: params
                });
            },
            select: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/printers/logs/select',
                    params: params
                });
            }
        };
    });
