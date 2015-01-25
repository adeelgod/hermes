/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('ReportSvc', function ($http) {
        // Public API here
        return {
            list: function() {
                return $http({
                    method: 'GET',
                    url: 'api/reports'
                });
            },
            report: function(params) {
                return $http({
                    method: 'POST',
                    url: 'api/reports',
                    data: params
                    //params: {responseType: "arraybuffer"}
                });
            }
        };
    });
})();
