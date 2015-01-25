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
            assign: function(params) {
                return $http({
                    method: 'POST',
                    url: 'api/bank/statements/assign',
                    data: params
                });
            },
            ignore: function(params) {
                return $http({
                    method: 'POST',
                    url: 'api/bank/statements/ignore',
                    data: params
                });
            },
            reset: function(params) {
                return $http({
                    method: 'POST',
                    url: 'api/bank/statements/reset',
                    data: params
                });
            }
        };
    });
})();
