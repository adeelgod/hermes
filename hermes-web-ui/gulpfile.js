'use strict';

var gulp = require('gulp');
var runSequence = require('run-sequence');
var gettext = require('gulp-angular-gettext');
//var downloadatomshell = require('gulp-download-atom-shell');
//var gulpAtom = require('gulp-atom');

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
    return gulp.src(".tmp/parts/*.tpl.html")
        .pipe($.minifyHtml({
            empty: true,
            spare: true,
            quotes: true
        }))
        .pipe($.ngHtml2js({
            moduleName: "hlu.ui.parts",
            prefix: "parts/"
        }))
        .pipe($.concat("parts.tpl.js"))
        .pipe(gulp.dest(".tmp/scripts"))
        .pipe($.size());

    gulp.src(".tmp/views/*.html")
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

gulp.task('styles-embed', function () {
    return gulp.src('app/bower_components/flags-sprite/dist/flags.css')
        .pipe($.cssBase64({
            verbose: true,
            baseDir: './',
            maxWeightResource: 100000,
            extensionsAllowed: ['.png']
        }))
        .pipe(gulp.dest('.tmp/styles'))
        .pipe($.size());
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
        .pipe($.uglify())
        .pipe($.size());
});

gulp.task('html', function () {
    var jsFilter = $.filter('**/*.js');
    var cssFilter = $.filter('**/*.css');

    //return gulp.src(['.tmp/**/*.html', '!.tmp/parts/*.html', '!.tmp/views/*.html'])
    return gulp.src(['.tmp/**/*.html'])
        .pipe($.plumber({
            errorHandler: onError
        }))
        .pipe($.useref.assets({searchPath: '{.tmp,app}'}))
        .pipe(jsFilter)
        //.pipe($.sourcemaps.init())
        .pipe($.ngAnnotate())
        .pipe($.uglify())
        //.pipe($.sourcemaps.write('.'))
        .pipe(jsFilter.restore())
        .pipe(cssFilter)
        .pipe($.csso())
        .pipe(cssFilter.restore())
        .pipe($.rev())
        .pipe($.useref.restore())
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
    return gulp.src(['app/*.*', 'app/**/*.json', '!app/bower_components/**/*.json', '!app/*.html', '!app/**/*.jade'], { dot: true })
        .pipe(gulp.dest('dist'));
});

// clean
gulp.task('clean', function () {
    return gulp.src(['.tmp', 'dist'], { read: false })
        .pipe($.clean());
});

// build
gulp.task('build', function(callback) {
    runSequence('styles', 'styles-embed', 'scripts', 'jade', 'html2js', 'html', 'pot', 'translations', 'images', 'fonts', 'extras', callback);
});

gulp.task('default', ['clean'], function () {
    gulp.start('build');
});

// connect
gulp.task('connect', function () {
    var connect = require('connect');
    var app = connect()
        .use(require('connect-livereload')({ port: 35729 }))
        .use(connect.static('app'))
        .use(connect.static('.tmp'))
        .use(connect.directory('app'));

    require('http').createServer(app)
        .listen(9000)
        .on('listening', function () {
            console.log('Started connect web server on http://localhost:9000');
        });
});

// serve
gulp.task('serve', ['connect'], function () {
    require('opn')('http://localhost:9000');
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
gulp.task('watch', ['connect', 'serve'], function () {
    var server = $.livereload();

    // watch for changes

    gulp.watch([
        '.tmp/**/*.html',
        '.tmp/styles/**/*.css',
        '.tmp/scripts/**/*.js',
        'app/scripts/**/*.js',
        'app/images/**/*'
    ]).on('change', function (file) {
        server.changed(file.path);
    });

    gulp.watch('app/styles/**/*.less', ['styles']);
    gulp.watch('app/scripts/**/*.js', ['scripts']);
    gulp.watch('app/images/**/*', ['images']);
    gulp.watch('app/content/**/*.md', ['jade']);
    gulp.watch('app/**/*.jade', ['jade']);
    gulp.watch('app/po/*.po', ['translations']);
    gulp.watch('bower.json', ['wiredep']);
    gulp.watch('gulpfile.js', ['build']);
});
