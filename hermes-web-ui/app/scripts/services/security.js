/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('SecuritySvc', function ($rootScope, $http, $state, $alert, HermesApi) {
        // Public API here
        var authenticated = false;

        return {
            login: function(login) {
                return $http({
                    method: 'POST',
                    url: HermesApi.baseUrl + 'api/security/login',
                    data: login
                }).success(function(data) {
                    authenticated = true;
                    $alert({content: 'Login success!', placement: 'top', type: 'success', show: true, duration: 5});
                    $rootScope.$broadcast('hermes.authenticated', true);
                    $state.go('home');
                }).error(function(data) {
                    authenticated = false;
                    $alert({content: 'Login failed!', placement: 'top', type: 'danger', show: true, duration: 5});
                    $rootScope.$broadcast('hermes.authenticated', false);
                });
            },
            logout: function() {
                authenticated = false;
                $rootScope.$broadcast('hermes.authenticated', false);
            },
            isAuthenticated: function() {
                return authenticated;
            }
        };
    });
})();
