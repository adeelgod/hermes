'use strict';

angular.module('hermes.ui')
    .factory('LanguageSvc', function (gettextCatalog) {
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
