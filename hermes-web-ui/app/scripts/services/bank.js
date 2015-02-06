/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('BankSvc', function ($http) {
        // Public API here
        return {
            list: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/bank/statements',
                    params: params
                });
            },
            filter: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/bank/statements/filter',
                    params: params
                });
            },
            process: function(params) {
                return $http({
                    method: 'POST',
                    url: 'api/bank/statements/process',
                    data: params
                });
            }
        };
    });
})();
