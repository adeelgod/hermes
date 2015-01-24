'use strict';

var gulp = require('gulp');
var runSequence = require('run-sequence');
var gettext = require('gulp-angular-gettext');

// load plugins
var $ = require('gulp-load-plugins')();

process.NODE_ENV = 'test';

// error handler
// NOTE: see here explanation http://cameronspear.com/blog/how-to-handle-gulp-watch-errors-with-plumber/
var onError = function (err) {
    //gutil.beep();
    console.error(err);
    throw err;
};

gulp.task('pot', function () {
    return gulp.src(['.tmp/**/*.html', '.tmp/scripts/**/*.js', 'app/scripts/**/*.js'])
        .pipe(gettext.extract('template.pot', {
            // options to pass to angular-gettext-tools...
        }))
        .pipe(gulp.dest('app/po/'));
});

gulp.task('translations', function () {
    return gulp.src('app/po/**/*.po')
        .pipe(gettext.compile({
            // options to pass to angular-gettext-tools...
            format: 'json'
        }))
        .pipe(gulp.dest('app/data/translations/'));
});

// converts templates to Javascript
gulp.task('html2js', function () {
    return gulp.src([".tmp/views/*.html"])
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
        .pipe(gulp.dest(".tmp/scripts"))
        .pipe($.size());
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
        .pipe(gulp.dest('.tmp/content'))
        .pipe($.size());
});

gulp.task('jade', ['markdown'], function () {
    // NOTE: pretty option is extremely important; otherwise the html pipeline breaks
    return gulp.src(['app/**/*.jade', '!app/layouts/**/*.jade', '!app/bower_components/**/*.jade'])
        .pipe($.jade({
            basedir: 'app',
            pretty: true,
            data: {
                debug: false
            }
        }))
        .pipe($.replace('@@iTunesId', 'TODO_ITUNES_ID'))
        .pipe($.replace('@@googleSiteVerification', 'TODO_GOOGLE_SITE_VERIFICATION'))
        .pipe(gulp.dest('.tmp'));
});

gulp.task('styles', function () {
    return gulp.src('app/styles/main.less')
        .pipe($.less())
        .pipe($.autoprefixer('last 1 version'))
        .pipe(gulp.dest('.tmp/styles'))
        .pipe($.size());
});

// Scripts
gulp.task('scripts', function () {
    return gulp.src('app/scripts/**/*.js')
        .pipe($.jshint())
        .pipe($.jshint.reporter(require('jshint-stylish')))
        .pipe($.ngAnnotate())
        .pipe(gulp.dest('.tmp/scripts'))
        .pipe($.size());
});

gulp.task('html', function () {
    var assets = $.useref.assets();

    return gulp.src(['.tmp/**/*.html'])
        .pipe($.plumber({
            errorHandler: onError
        }))
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
        .pipe(gulp.dest('dist'))
        .pipe($.size());
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
    return gulp.src(['app/bower_components/**/fonts/*.{eot,otf,svg,ttf,woff}', 'app/bower_components/**/font/*.{eot,otf,svg,ttf,woff}'])
        .pipe($.flatten())
        .pipe(gulp.dest('dist/fonts'))
        .pipe($.size());
});

// extras
gulp.task('extras', function () {
    return gulp.src(['app/*.*', 'app/**/*.json', 'app/**/*.mp3', 'app/**/*.webm', '!app/bower_components/**/*.json', '!app/bower_components/**/*.mp3', '!app/*.html', '!app/**/*.jade'], { dot: true })
        .pipe(gulp.dest('dist'));
});

// clean
gulp.task('clean', function () {
    return gulp.src(['.tmp', 'dist'], { read: false })
        .pipe($.rimraf());
});

// build
gulp.task('build', function() {
    runSequence('styles', 'scripts', 'jade', 'html2js', 'html', 'pot', 'translations', 'images', 'fonts', 'extras');
});

// connect
gulp.task('connectDev', function () {
    $.connect.server({
        root: ['app', '.tmp', 'app/bower_components/font-awesome'],
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

// inject bower components
gulp.task('wiredep', function () {
    var wiredep = require('wiredep').stream;

    // TODO: only change this in layout
    gulp.src('app/layouts/default/*.jade')
        .pipe(wiredep({
            directory: 'app/bower_components'
        }))
        .pipe(gulp.dest('app'));
});

// watch
gulp.task('watch', ['connectDev'], function () {
    // watch for changes

    gulp.watch([
        '.tmp/**/*.html',
        '.tmp/styles/**/*.css',
        '.tmp/scripts/**/*.js',
        'app/scripts/**/*.js',
        'app/images/**/*'
    ]);

    gulp.watch('app/styles/**/*.less', ['styles']);
    gulp.watch('app/scripts/**/*.js', ['scripts']);
    gulp.watch('app/images/**/*', ['images']);
    gulp.watch('app/content/**/*.md', ['jade']);
    gulp.watch('app/**/*.jade', ['jade']);
    gulp.watch('app/po/*.po', ['translations']);
    gulp.watch('bower.json', ['wiredep']);
    gulp.watch('gulpfile.js', ['build']);
});

//gulp.task('default', ['connectDist', 'connectDev', 'watch']);

gulp.task('default', ['clean', 'build', 'watch']);
