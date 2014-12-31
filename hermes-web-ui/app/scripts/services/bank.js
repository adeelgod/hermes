'use strict';

angular.module('hermes.ui')
    .factory('BankSvc', function ($http) {
        // Public API here
        return {
            list: function(params) {
                return $http({
                    method: 'GET',
                    url: 'api/bank/statements',
                    params: params
                });
            }
        };
    });
