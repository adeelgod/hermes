'use strict';

angular.module('hermes.ui')
    .factory('ExampleSvc', function ($http) {
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
