/** global: angular */

'use strict';

angular.module('hermes.ui', [
    'ngCookies',
    'ngAnimate',
    'ngTouch',
    'ngSanitize',
    'gettext',
    'mgcrea.ngStrap',
    'ui.router',
    'xeditable',
    'ngProgressLite',
    'angularFileUpload',
    'restangular', // TODO: decide which one to use
    'angularMoment'
])
.config(function ($locationProvider, $stateProvider, $urlRouterProvider, RestangularProvider, $modalProvider, $asideProvider, $sceDelegateProvider, ngProgressLiteProvider) {
    //$interpolateProvider.startSymbol('{[').endSymbol(']}');

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
        .state('configuration', {
            abstract: false,
            url: '/configuration',
            templateUrl: 'views/configuration.html',
            controller: 'ConfigurationCtrl'
        })
        .state('form', {
            abstract: false,
            url: '/form',
            templateUrl: 'views/form.html',
            controller: 'FormCtrl'
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
        })
        .state('terms', {
            abstract: false,
            url: '/terms',
            templateUrl: 'views/terms.html',
            controller: 'TermsCtrl'
        });

    // Restangular
    RestangularProvider.setBaseUrl('http://localhost:8080/');
    RestangularProvider.setDefaultHttpFields({cache: true});

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

    ngProgressLiteProvider.settings.speed = 1500;

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
}).run(function(editableOptions, LanguageSvc) {
    editableOptions.theme = 'bs3';
    LanguageSvc.language('en');
});
