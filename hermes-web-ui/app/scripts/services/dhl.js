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
            },
            trackingCheck: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/dhl/tracking/check',
                    params: params
                });
            },
            trackingCheckStatus: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/dhl/tracking/check/status',
                    params: params
                });
            },
            trackingCheckCancel: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/dhl/tracking/check/cancel',
                    params: params
                });
            }
        };
    });
