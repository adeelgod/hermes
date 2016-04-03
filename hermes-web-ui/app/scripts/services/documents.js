/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('DocumentsSvc', function ($http, HermesApi) {
        // Public API here
        return {
            printJob: function(params) {
                return $http({
                    method: 'POST',
                    url: HermesApi.baseUrl + 'api/documents/print',
                    data: params
                });
            },
        };
    });
})();
