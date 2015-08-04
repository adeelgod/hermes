/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('UtilSvc', function ($http, HermesApi) {
        // Public API here
        return {
            netdrive: function() {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/util/netdrive'
                });
            }
        };
    });
})();
