'use strict';

angular.module('hermes.ui')
    .factory('DhlSvc', function ($http) {
        // Public API here
        return {
            trackingStatus: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/dhl/tracking/status',
                    params: params
                });
            }
        };
    });
