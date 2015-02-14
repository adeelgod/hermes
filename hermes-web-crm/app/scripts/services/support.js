/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.crm.service');
    } catch (e) {
        module = angular.module('hermes.crm.service', []);
    }

    module.factory('SupportSvc', function ($http) {
        // Public API here
        return {
            send: function(params) {
                return $http({
                    method: 'POST',
                    url: 'api/support',
                    data: params
                });
            }
        };
    });
})();
