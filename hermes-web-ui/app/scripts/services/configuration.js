/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('ConfigurationSvc', function ($http, HermesApi) {
        // Public API here
        return {
            list: function() {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/configuration'
                });
            },
            save: function(config) {
                return $http({
                    method: 'POST',
                    url: HermesApi.baseUrl + 'api/configuration',
                    data: config
                });
            }
        };
    });
})();
