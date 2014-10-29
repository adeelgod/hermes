/*global printStackTrace */

'use strict';

angular.module('hermes.ui')
    .factory('StacktraceSvc', function () {
        // Service logic
        // ...

        // Public API here
        return {
            print: printStackTrace
        };
    });
