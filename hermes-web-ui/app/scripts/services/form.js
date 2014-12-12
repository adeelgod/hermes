'use strict';

angular.module('hermes.ui')
    .factory('FormSvc', function ($http, $alert) {
        // Public API here
        return {
            list: function() {
                return $http({
                    method: 'GET',
                    url: 'api/forms'
                });
            },
            get: function(name) {
                return $http({
                    method: 'GET',
                    url: 'api/forms/' + name
                });
            },
            query: function(params) {
                return $http({
                    method: 'POST',
                    url: 'api/forms/query',
                    data: params
                });
            },
            download: function(params) {
                return $http({
                    method: 'POST',
                    url: 'api/forms/download',
                    data: params
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
            },
            synchronize: function(scope) {
                scope.loading = true;
                var params = {};
                params['_form'] = 'update';
                this.query(params).success(function(data) {
                    scope.loading = false;
                    $alert({content: 'Sync success!', placement: 'top', type: 'success', show: true, duration: 5});
                }).error(function(data) {
                    scope.loading = false;
                    $alert({content: 'Sync failed!', placement: 'top', type: 'danger', show: true, duration: 5});
                });
            }
        };
    });
