/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('BankPatternSvc', function ($http, HermesApi) {
        // Public API here
        return {
            list: function(params) {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/bank/patterns',
                    params: params
                });
            },
            save: function(params) {
                return $http({
                    method: 'POST',
                    url: HermesApi.baseUrl + 'api/bank/patterns',
                    data: params
                });
            },
            remove: function(params) {
                return $http({
                    method: 'DELETE',
                    url: HermesApi.baseUrl + 'api/bank/patterns',
                    params: params
                });
            }
        };
    });
})();
