/* global printStackTrace */
/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.crm.service');
    } catch (e) {
        module = angular.module('hermes.crm.service', []);
    }

    module.factory('StacktraceSvc', function () {
        // Service logic
        // ...

        // Public API here
        return {
            print: printStackTrace
        };
    });
})();
