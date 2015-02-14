/* global angular */

(function(module) {
    'use strict';

    angular.module('hermes.ui', [
        'ngCookies',
        'ngAnimate',
        'ngTouch',
        'ngSanitize',
        'hermes.crm.views',
        'hermes.crm.service',
        'hermes.crm.controller',
        'gettext',
        'ngAudio',
        'mgcrea.ngStrap',
        'ui.router',
        'xeditable',
        'angularFileUpload'
    ]).config(function ($locationProvider, $stateProvider, $urlRouterProvider, $modalProvider, $asideProvider, $sceDelegateProvider) {
        // TODO: remove this in production; just to avoid CORS problems
        // see here more: https://docs.angularjs.org/api/ng/provider/$sceDelegateProvider#resourceUrlWhitelist
        $sceDelegateProvider.resourceUrlWhitelist([
            // Allow same origin resource loads.
            'self',
            // Allow loading from our assets domain.  Notice the difference between * and **.
            'http://localhost:8080/**']);

        $locationProvider
            .html5Mode(false)
            .hashPrefix('!');

        $urlRouterProvider.when('', '/');

        $stateProvider
            .state('home', {
                abstract: false,
                url: '/',
                templateUrl: 'views/home.html',
                controller: 'HomeCtrl'
            })
            .state('about', {
                abstract: false,
                url: '/about',
                templateUrl: 'views/about.html',
                controller: 'AboutCtrl'
            })
            .state('signin', {
                abstract: false,
                url: '/signin',
                templateUrl: 'views/signin.html',
                controller: 'SignInCtrl'
            });

        angular.extend($modalProvider.defaults, {
            html: true,
            animation: 'am-flip-x'
        });

        angular.extend($asideProvider.defaults, {
            html: true,
            //backdrop: 'static',
            container: 'body',
            animation: 'am-fade-and-slide-left',
            placement: 'top'
        });

        //$httpProvider.interceptors.push(hluHttpInterceptor);

    })
    .factory('$exceptionHandler', function ($log, $window, StacktraceSvc) {
        return function (exception, cause) {

            try {

                var errorMessage = exception.toString();
                var stackTrace = StacktraceSvc.print({ e: exception });

                // Log the JavaScript error to the server.
                // --
                // NOTE: In this demo, the POST URL doesn't
                // exists and will simply return a 404.
                $log.error(angular.toJson({
                        errorUrl: $window.location.href,
                        errorMessage: errorMessage,
                        stackTrace: stackTrace,
                        cause: ( cause || '' )
                    })
                );

            } catch ( loggingError ) {

                // For Developers - log the log-failure.
                $log.warn( 'Error logging failed' );
                $log.log( loggingError );

            }
            //$log.error('This is the cause: ' + cause);
            //exception.message += ' (caused by "' + cause + '")';
            //throw exception;
        };
    }).run(function($rootScope, editableOptions, LanguageSvc) {
            editableOptions.theme = 'bs3';
            LanguageSvc.language('en');

            $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
                if(toState && toState.name!=='shipping') {
                    $rootScope.$broadcast('hermes.sound.off');
                }
            });
        });
})();
