/* global angular */

(function(module) {
    'use strict';

    angular.module('hermes.ui', [
        'ngCookies',
        'ngAnimate',
        'ngTouch',
        'ngSanitize',
        'hermes.ui.views',
        'hermes.ui.service',
        'hermes.ui.controller',
        'gettext',
        'ngAudio',
        'mgcrea.ngStrap',
        'ui.router',
        'ui.codemirror',
        'xeditable',
        'angularFileUpload'
    ]).constant('HermesApi', {
            baseUrl: ''
            //baseUrl: 'http://localhost:8081/'
        }
    ).config(function ($locationProvider, $stateProvider, $urlRouterProvider, $modalProvider, $asideProvider, $sceDelegateProvider) {
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
                templateUrl: 'views/orders.html',
                controller: 'OrderCtrl'
            })
            .state('shipping', {
                abstract: false,
                url: '/shipping',
                templateUrl: 'views/shipping.html',
                controller: 'ShippingCtrl'
            })
            .state('bank', {
                abstract: false,
                url: '/bank',
                templateUrl: 'views/bank/detail.html',
                controller: 'BankCtrl'
            })
            .state('bank_step1', {
                abstract: false,
                url: '/bank_step1',
                templateUrl: 'views/bank/step1/detail.html'
            })
            .state('bank_step2', {
                abstract: false,
                url: '/bank_step2',
                templateUrl: 'views/bank/step1/detail.html'
            })
            .state('bank_pattern_edit', {
                abstract: false,
                url: '/pattern/edit/:id',
                templateUrl: 'views/bank/pattern/edit.html',
                controller: 'BankPatternCtrl'
            })
            .state('bank_pattern_list', {
                abstract: false,
                url: '/pattern/list',
                templateUrl: 'views/bank/pattern/list.html',
                controller: 'BankPatternListCtrl'
            })
            .state('configuration', {
                abstract: false,
                url: '/configuration',
                templateUrl: 'views/configuration.html',
                controller: 'ConfigurationCtrl'
            })
            .state('form_edit', {
                abstract: false,
                url: '/form/edit/:id',
                templateUrl: 'views/form/edit.html',
                controller: 'FormCtrl'
            })
            .state('form_list', {
                abstract: false,
                url: '/form/list',
                templateUrl: 'views/form/list.html',
                controller: 'FormListCtrl'
            })
            .state('form_execute', {
                abstract: false,
                url: '/form/execute/:id',
                templateUrl: 'views/form/execute.html',
                controller: 'FormExecuteCtrl'
            })
            .state('about', {
                abstract: false,
                url: '/about',
                templateUrl: 'views/about.html',
                controller: 'AboutCtrl'
            })
            .state('printers', {
                abstract: false,
                url: '/printers',
                templateUrl: 'views/printers.html',
                controller: 'PrinterCtrl'
            })
            .state('orders', {
                abstract: false,
                url: '/orders',
                templateUrl: 'views/orders.html',
                controller: 'OrderCtrl'
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

        //growlProvider.globalTimeToLive(5000);
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
