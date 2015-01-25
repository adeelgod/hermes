/* global require */
'use strict';

var gulp = require('gulp');

// load plugins
var $ = require('gulp-load-plugins')();

gulp.task('translations', ['pot'], function () {
    var gettext = require('gulp-angular-gettext');

    return gulp.src('app/po/**/*.po')
        .pipe(gettext.compile({
            // options to pass to angular-gettext-tools...
            format: 'json'
        }))
        .pipe(gulp.dest('app/data/translations/'));
});

gulp.task('pot', function () {
    var gettext = require('gulp-angular-gettext');

    return gulp.src(['.tmp/**/*.html', 'app/scripts/**/*.js'])
        .pipe(gettext.extract('template.pot', {
            // options to pass to angular-gettext-tools...
        }))
        .pipe(gulp.dest('app/po/'));
});

gulp.task('html', ['jade'], function () {
    var assets = $.useref.assets();

    return gulp.src(['.tmp/**/*.html'])
        .pipe(assets)
        .pipe($.if('*.js', $.sourcemaps.init()))
        .pipe($.if('*.js', $.uglify()))
        .pipe($.if('*.js', $.sourcemaps.write()))
        .pipe($.if('*.css', $.sourcemaps.init()))
        .pipe($.if('*.css', $.csso()))
        .pipe($.if('*.css', $.sourcemaps.write()))
        .pipe(assets.restore())
        .pipe($.if('*.js', $.rev()))
        .pipe($.if('*.css', $.rev()))
        .pipe($.useref())
        .pipe($.revReplace())
        .pipe(gulp.dest('dist'));
});

// Scripts
gulp.task('scripts', function () {
    return gulp.src('app/scripts/**/*.js')
        .pipe($.jshint())
        .pipe($.jshint.reporter(require('jshint-stylish')))
        .pipe($.ngAnnotate({
            remove: true,
            add: true,
            single_quotes: true
        }))
        .pipe(gulp.dest('.tmp/scripts'));
});

gulp.task('jade', ['markdown', 'scripts', 'styles'], function () {
    var bowerFiles = require('main-bower-files');

    // NOTE: pretty option is extremely important; otherwise the html pipeline breaks
    return gulp.src(['app/**/*.jade', '!app/layouts/**/*.jade'])
        .pipe($.jade({
            basedir: 'app',
            pretty: true,
            data: {
                debug: false
            }
        }))
        .pipe($.replace('@@iTunesId', 'TODO_ITUNES_ID'))
        .pipe($.replace('@@googleSiteVerification', 'TODO_GOOGLE_SITE_VERIFICATION'))
        .pipe($.inject(
            gulp.src(bowerFiles(), {read: false}),
            {name: 'vendor', relative: true}
        ))
        .pipe($.inject(
            gulp.src('.tmp/**/*.js', {read: false}).pipe($.angularFilesort()),
            {name: 'angular', relative: true}
        ))
        .pipe(gulp.dest('.tmp'))
        .pipe($.filter(['views/**/*.html']))
        .pipe($.minifyHtml({
            empty: true,
            spare: true,
            quotes: true
        }))
        .pipe($.ngHtml2js({
            moduleName: "hermes.ui.views",
            prefix: "views/"
        }))
        .pipe($.concat("views.tpl.js"))
        .pipe(gulp.dest(".tmp/scripts"));
});

// converts markdown to HTML
gulp.task('markdown', function () {
    return gulp.src(['app/content/**/*.md'])
        .pipe($.markdown())
        .pipe($.minifyHtml({
            empty: true,
            spare: true,
            quotes: true
        }))
        .pipe(gulp.dest('.tmp/content'));
});

gulp.task('styles', function () {
    return gulp.src('app/styles/main.less')
        .pipe($.less())
        .pipe($.autoprefixer('last 1 version'))
        .pipe(gulp.dest('.tmp/styles'));
});

gulp.task('images', function () {
    return gulp.src('app/images/**/*')
        .pipe($.cache($.imagemin({
            optimizationLevel: 3,
            progressive: true,
            interlaced: true
        })))
        .pipe(gulp.dest('dist/images'))
        .pipe($.size());
});

// fonts
gulp.task('fonts', function () {
    return gulp.src(['bower_components/**/fonts/*.{eot,otf,svg,ttf,woff}', 'bower_components/**/font/*.{eot,otf,svg,ttf,woff}'])
        .pipe($.flatten())
        .pipe(gulp.dest('dist/fonts'))
        .pipe($.size());
});

// extras
gulp.task('extras', function () {
    return gulp.src(['app/**/*.json', 'app/**/*.mp3', 'app/**/*.webm'], { dot: true })
        .pipe(gulp.dest('dist'));
});

// clean
gulp.task('clean', function () {
    return gulp.src(['.tmp', 'dist'], { read: false })
        .pipe($.rimraf());
});

gulp.task('clean-build', ['clean'], function () {
    gulp.run('build');
});

// build
gulp.task('build', ['html', 'images', 'fonts', 'extras'], function() {
});

// connect
gulp.task('connectDev', function () {
    $.connect.server({
        root: ['app', '.tmp', 'bower_components/font-awesome'],
        port: 8000,
        livereload: true
    });
});

gulp.task('connectDist', function () {
    $.connect.server({
        root: 'dist',
        port: 8001,
        livereload: true
    });
});

// watch
gulp.task('watch', ['connectDev'], function () {
    // watch for changes

    gulp.watch('app/styles/**/*.less', ['styles']);
    gulp.watch('app/scripts/**/*.js', ['scripts']);
    gulp.watch('app/images/**/*', ['images']);
    gulp.watch('app/content/**/*.md', ['jade']);
    gulp.watch('app/**/*.jade', ['jade']);
    gulp.watch('app/po/*.po', ['translations']);
    gulp.watch('bower.json', ['jade']);
    gulp.watch('gulpfile.js', ['build']);
});

//gulp.task('default', ['connectDist', 'connectDev', 'watch']);

gulp.task('default', ['build', 'watch']);
