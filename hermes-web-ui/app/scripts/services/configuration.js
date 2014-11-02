'use strict';

angular.module('hermes.ui')
    .factory('ConfigurationSvc', function ($http) {
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
                    url: 'api/configuration'
                });
            }
        };
    });
