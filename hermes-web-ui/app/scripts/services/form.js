'use strict';

angular.module('hermes.ui')
    .factory('FormSvc', function ($http) {
        // Public API here
        return {
            listForms: function() {
                return $http({
                    method: 'GET',
                    url: 'api/forms'
                });
            },
            listFields: function(form) {
                return $http({
                    method: 'GET',
                    url: 'api/forms/' + form
                });
            },
            save: function(form) {
                return $http({
                    method: 'POST',
                    url: 'api/forms',
                    data: form
                });
            },
            remove: function(params) {
                return $http({
                    method: 'DELETE',
                    url: 'api/forms',
                    params: params
                });
            }
        };
    });
