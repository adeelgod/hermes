/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('ShippingSvc', function ($http, HermesApi) {
        // Public API here
        return {
            label: function(params) {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/shipping/label',
                    params: params
                });
            },
            shipment: function(params) {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/shipping/shipment',
                    params: params
                });
            },
            status: function(params) {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/shipping/status',
                    params: params
                });
            }
        };
    });
})();
