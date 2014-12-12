'use strict';

angular.module('hermes.ui')
    .factory('ShippingSvc', function ($http) {
        // Public API here
        return {
            label: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/shipping/label',
                    params: params
                });
            },
            shipment: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/shipping/shipment',
                    params: params
                });
            },
            status: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/shipping/status',
                    params: params
                });
            }
        };
    });
