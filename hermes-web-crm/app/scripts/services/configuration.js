/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.crm.service');
    } catch (e) {
        module = angular.module('hermes.crm.service', []);
    }

    module.factory('ConfigurationSvc', function ($http) {
        // Public API here
        return {
            list: function() {
                return $http({
                    method: 'GET',
                    url: 'api/configuration'
                });
            },
            save: function(config) {
                return $http({
                    method: 'POST',
                    url: 'api/configuration',
                    data: config
                });
            }
        };
    });
})();
