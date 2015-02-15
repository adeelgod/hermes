(function(module) {
    'use strict';

    try {
        module = angular.module('hermes.crm.controller');
    } catch (e) {
        module = angular.module('hermes.crm.controller', []);
    }

    module.controller('ConfigurationCtrl', function ($scope, $log, ConfigurationSvc, FileUploader) {
        $scope.loading = true;

        $scope.database = {};

        $scope.databases = [
            {'name': 'H2 (embedded)', 'driver': 'org.h2.Driver', 'dialect': 'org.hibernate.dialect.H2Dialect', 'url': 'jdbc:h2:mem:', 'username': 'sa', 'password': ''},
            {'name': 'MySQL', 'driver': 'com.mysql.jdbc.Driver', 'dialect': 'org.hibernate.dialect.MySQLDialect', 'url': 'jdbc:mysql://127.0.0.1:13306/', 'username': 'print', 'password': ''}
        ];

        $scope.printMethods = [
            {'name': 'JAVA'},
            {'name': 'IMAGE'},
            {'name': 'PDFBOX'},
            {'name': 'QUOPPA'},
            {'name': 'BOF'},
            {'name': 'SWINGLABS'},
            {'name': 'PDFBOXPAGEABLE'},
            {'name': 'SMARTJ'},
            //{'name': 'ICEPDF'},
            //{'name': 'JZEBRA'},
            {'name': 'GHOSTSCRIPT'},
            {'name': 'ACROBAT'}
        ];

        $scope.imageFormats = [
            {'name': 'PNG'},
            {'name': 'JPG'},
            {'name': 'GIF'}
        ];

        $scope.tab = 'database';

        $scope.updateDatabase = function() {
            $scope.configuration['hibernate.dialect'] = $scope.database.dialect;
            $scope.configuration['hermes.db.auswertung.driver'] = $scope.database.driver;
            $scope.configuration['hermes.db.auswertung.url'] = $scope.database.url + 'Auswertung';
            $scope.configuration['hermes.db.auswertung.username'] = $scope.database.username;
            $scope.configuration['hermes.db.auswertung.password'] = $scope.database.password;
            $scope.configuration['hermes.db.lcarb.driver'] = $scope.database.driver;
            $scope.configuration['hermes.db.lcarb.url'] = $scope.database.url + 'l_carb_shop_de';
            $scope.configuration['hermes.db.lcarb.username'] = $scope.database.username;
            $scope.configuration['hermes.db.lcarb.password'] = $scope.database.password;
        };

        $scope.$watch('database', function() {
            $scope.updateDatabase();
        });

        $scope.save = function() {
            $scope.loading = true;
            ConfigurationSvc.save($scope.configuration).success(function(data) {
                $scope.loading = false;
            }).error(function(data) {
                $scope.loading = false;
            });
        };

        $scope.refresh = function() {
            $scope.loading = true;
            ConfigurationSvc.list().success(function(data) {
                //$scope.configuration = data;
                $scope.configuration = data.properties;
                $scope.printers = data.printers;
                $scope.forms = data.forms;
                $scope.reports = data.templates;
                $scope.loading = false;
            });
        };

        var uploader = $scope.uploader = new FileUploader({
            url: 'api/configuration/templates'
        });

        // FILTERS
        uploader.filters.push({
            name: 'customFilter',
            fn: function(item /*{File|FileLikeObject}*/, options) {
                return this.queue.length < 10;
            }
        });

        // CALLBACKS
        uploader.onWhenAddingFileFailed = function(item /*{File|FileLikeObject}*/, filter, options) {
            $log.info('onWhenAddingFileFailed', item, filter, options);
        };
        uploader.onAfterAddingFile = function(fileItem) {
            $log.info('onAfterAddingFile', fileItem);
        };
        uploader.onAfterAddingAll = function(addedFileItems) {
            $log.info('onAfterAddingAll', addedFileItems);
        };
        uploader.onBeforeUploadItem = function(item) {
            $log.info('onBeforeUploadItem', item);
        };
        uploader.onProgressItem = function(fileItem, progress) {
            $log.info('onProgressItem', fileItem, progress);
        };
        uploader.onProgressAll = function(progress) {
            $log.info('onProgressAll', progress);
        };
        uploader.onSuccessItem = function(fileItem, response, status, headers) {
            $log.info('onSuccessItem', fileItem, response, status, headers);
        };
        uploader.onErrorItem = function(fileItem, response, status, headers) {
            $log.info('onErrorItem', fileItem, response, status, headers);
        };
        uploader.onCancelItem = function(fileItem, response, status, headers) {
            $log.info('onCancelItem', fileItem, response, status, headers);
        };
        uploader.onCompleteItem = function(fileItem, response, status, headers) {
            $log.info('onCompleteItem', fileItem, response, status, headers);
        };
        uploader.onCompleteAll = function() {
            $log.info('onCompleteAll');
        };

        $scope.refresh();
    });
})();
