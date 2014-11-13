'use strict';

angular.module('hermes.ui').controller('ConfigurationCtrl', function ($scope, $log, ConfigurationSvc, FileUploader, FormSvc, PrinterSvc, ReportSvc) {
    $scope.database = {};

    $scope.databases = [
        {'name': 'H2 (embedded)', 'driver': 'org.h2.Driver', 'dialect': 'org.hibernate.dialect.H2Dialect', 'url': 'jdbc:h2:mem:Auswertung', 'username': 'sa', 'password': ''},
        {'name': 'MySQL', 'driver': 'com.mysql.jdbc.Driver', 'dialect': 'org.hibernate.dialect.MySQLDialect', 'url': 'jdbc:mysql://127.0.0.1:13306/Auswertung', 'username': 'print', 'password': ''}
    ];

    $scope.printMethods = [
        {'name': 'JAVA'},
        {'name': 'PDFBOX'},
        {'name': 'GHOSTSCRIPT'},
        {'name': 'ACROBAT'}
    ];

    $scope.tab = 'database';

    $scope.updateDatabase = function() {
        $scope.configuration['hermes.db.driver'] = $scope.database.driver;
        $scope.configuration['hibernate.dialect'] = $scope.database.dialect;
        $scope.configuration['hermes.db.url'] = $scope.database.url;
        $scope.configuration['hermes.db.username'] = $scope.database.username;
        $scope.configuration['hermes.db.password'] = $scope.database.password;
    };

    $scope.$watch('database', function() {
        $scope.updateDatabase();
    });

    $scope.save = function() {
        ConfigurationSvc.save($scope.configuration);
    };

    $scope.refresh = function() {
        ConfigurationSvc.list().success(function(data) {
            //$scope.configuration = data;
            $scope.configuration = data.properties;
            $scope.printers = data.printers;
            $scope.forms = data.forms;
            $scope.reports = data.templates;
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
