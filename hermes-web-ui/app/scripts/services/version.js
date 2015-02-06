/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('VersionSvc', function ($http) {
        // Public API here
        return {
            version: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/version',
                    params: params
                });
            }
        };
    });
})();
