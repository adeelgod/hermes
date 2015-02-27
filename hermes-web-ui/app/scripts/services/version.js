/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('VersionSvc', function ($http, HermesApi) {
        // Public API here
        return {
            version: function(params) {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/version',
                    params: params
                });
            }
        };
    });
})();
