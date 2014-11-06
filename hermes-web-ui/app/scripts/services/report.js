'use strict';

angular.module('hermes.ui')
    .factory('ReportSvc', function ($http) {
        // Public API here
        return {
            list: function() {
                return $http({
                    method: 'GET',
                    url: 'api/reports'
                });
            }
        };
    });
