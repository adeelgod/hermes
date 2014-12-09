'use strict';

angular.module('hermes.ui')
    .factory('FakeShippingSvc', function ($http) {
        // Public API here
        return {
            label: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/fake/shipping/label',
                    params: params
                });
            },
            shipment: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/fake/shipping/shipment',
                    params: params
                });
            }
        };
    });
