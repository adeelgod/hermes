/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('BankSvc', function ($http, HermesApi) {
        // Public API here
        return {
            listMatched: function(params) {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/bank/statements/matched',
                    params: params
                });
            },
            listUnmatched: function(params) {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/bank/statements/unmatched',
                    params: params
                });
            },
            filter: function(params) {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/bank/statements/filter',
                    params: params
                });
            },
            process: function(params) {
                return $http({
                    method: 'POST',
                    url: HermesApi.baseUrl + 'api/bank/statements/process',
                    data: params
                });
            },
            save: function(params) {
                return $http({
                    method: 'POST',
                    url: HermesApi.baseUrl + 'api/bank/statements',
                    data: params
                });
            },
            processStatus: function(params) {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/bank/statements/process/status',
                    params: params
                });
            },
            processCancel: function(params) {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/bank/statements/process/cancel',
                    params: params
                });
            }
        };
    });
})();
