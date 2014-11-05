'use strict';

angular.module('hermes.ui')
    .factory('FormSvc', function ($http) {
        // Public API here
        return {
            list: function() {
                return $http({
                    method: 'GET',
                    url: 'api/forms'
                });
            },
            get: function(uid) {
                return $http({
                    method: 'GET',
                    url: 'api/forms/' + uid
                });
            },
            save: function(form) {
                return $http({
                    method: 'POST',
                    url: 'api/forms',
                    data: form
                });
            },
            saveField: function(field) {
                return $http({
                    method: 'POST',
                    url: 'api/forms/fields',
                    data: field
                });
            },
            remove: function(params) {
                return $http({
                    method: 'DELETE',
                    url: 'api/forms',
                    params: params
                });
            },
            removeField: function(params) {
                return $http({
                    method: 'DELETE',
                    url: 'api/forms/fields',
                    params: params
                });
            }
        };
    });
