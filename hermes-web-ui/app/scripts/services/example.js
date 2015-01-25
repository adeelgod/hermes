/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('ExampleSvc', function ($http) {
        // Public API here
        return {
            queue: function() {
                return $http({
                    method: 'GET',
                    url: 'api/examples/queue'
                });
            }
        };
    });
})();
