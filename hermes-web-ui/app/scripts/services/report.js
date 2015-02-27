/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('ReportSvc', function ($http, HermesApi) {
        // Public API here
        return {
            list: function() {
                return $http({
                    method: 'GET',
                    url: HermesApi.baseUrl + 'api/reports'
                });
            },
            report: function(params) {
                return $http({
                    method: 'POST',
                    url: HermesApi.baseUrl + 'api/reports',
                    data: params
                    //params: {responseType: "arraybuffer"}
                });
            }
        };
    });
})();
