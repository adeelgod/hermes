/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('BankPatternSvc', function ($http) {
        // Public API here
        return {
            list: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/bank/patterns',
                    params: params
                });
            },
            save: function(params) {
                return $http({
                    method: 'POST',
                    url: 'api/bank/patterns',
                    data: params
                });
            },
            remove: function(params) {
                return $http({
                    method: 'DELETE',
                    url: 'api/bank/patterns',
                    params: params
                });
            }
        };
    });
})();
