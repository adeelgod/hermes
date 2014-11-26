'use strict';

angular.module('hermes.ui')
    .factory('ShippingSvc', function ($http) {
        // Public API here
        return {
            status: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/shipping/status',
                    params: params
                });
            }
        };
    });
