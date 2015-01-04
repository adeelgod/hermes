'use strict';

angular.module('hermes.ui').factory('BankPatternSvc', function ($http) {
    // Public API here
    return {
        list: function(params) {
            return $http({
                method: 'GET',
                url: 'api/bank/patterns',
                params: params
            });
        },
        save: function(params) {
            return $http({
                method: 'POST',
                url: 'api/bank/patterns',
                data: params
            });
        },
        remove: function(params) {
            return $http({
                method: 'DELETE',
                url: 'api/bank/patterns',
                params: params
            });
        }
    };
});
