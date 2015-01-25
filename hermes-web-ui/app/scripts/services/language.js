/* global angular */

(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.ui.service');
    } catch (e) {
        module = angular.module('hermes.ui.service', []);
    }

    module.factory('LanguageSvc', function (gettextCatalog) {
        var lang ='en';

        gettextCatalog.debug = true;

        // Public API here
        return {
            language: function(value) {
                if(value) {
                    lang = value;
                    gettextCatalog.setCurrentLanguage(lang);
                    gettextCatalog.loadRemote('data/translations/' + lang + '.json');
                } else {
                    return lang;
                }
            }
        };
    });
})();
