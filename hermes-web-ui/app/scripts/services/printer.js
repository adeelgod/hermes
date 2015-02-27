/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('PrinterSvc', function ($http, HermesApi) {
        // Public API here
        return {
            printers: function() {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/printers'
                });
            },
            print: function(params) {
                return $http({
                    method: 'POST',
                    url: HermesApi.baseUrl + 'api/printers/print',
                    data: params
                });
            },
            printAll: function(params) {
                return $http({
                    method: 'POST',
                    url: HermesApi.baseUrl + 'api/printers/print/all',
                    data: params
                });
            },
            cancel: function() {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/printers/print/cancel'
                });
            },
            status: function() {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/printers/print/status'
                });
            }
        };
    });
})();
