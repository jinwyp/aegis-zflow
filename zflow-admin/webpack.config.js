var webpack = require('webpack')
var path = require('path')
var fs = require('fs');
var glob = require('glob');

var getEntry = function(){
    var entry = {};
    // glob.sync('./backend/**/*.html').forEach(function (name) {
    //     var ename = '/html' + name.slice(name.lastIndexOf('/backend/')+8, name.length);
    //     entry[ename] = name;
    // });

    var sArr = ['./frontend/img/**/*.*', './frontend/css/**/*.css', './frontend/json/**/*.json', './frontend/js/**/*.js', './frontend/lib/**/*.*'];
    sArr.forEach(function(s, si){
        glob.sync(s).forEach(function (pth) {
            var ename = pth.slice(pth.lastIndexOf('/frontend/')+10, pth.length);
            entry[ename] = pth;
        });
    })
console.log('-----log entry-------------------------------------------------------')
console.log(entry);
    return entry;
}

module.exports = { 
  entry: getEntry(),
  output: {
    path: path.join(__dirname, './static'),
    filename: './js/index.js',
    publicPath: '/zflow/static/'
  },
  node: {
    net: 'empty',
    tls: 'empty',
    dns: 'empty'
  },
  module: {
    loaders: [
      { test: /\.html$/,
        loader: 'file-loader?regExp=backend\/(.+)\?&name=html/[1]' },
      { test: /\/js\/(.)+\.(js)$/,
        loader: 'file-loader?regExp=frontend\/js\/(.+)\?&name=js/[1]' },
      { test: /\/lib\/(.)+\.(js|css|json|md|gzip|map)$/,
        loader: 'file-loader?regExp=frontend\/lib\/(.+)\?&name=lib/[1]' },
      { test: /\.(png|jpg|gif)$/,
        loader: 'file-loader?regExp=frontend\/img\/(.+)\?&name=img/[1]' },
      { test: /\.json$/,
        loader: 'file-loader?regExp=frontend\/json\/(.+)\?&name=json/[1]' },
      { test: /\/css\/(.)+\.css$/,
        loader: 'file-loader?regExp=frontend\/css\/(.+)\?&name=css/[1]' }
    ],
  },
  resolve: {
    extensions: ['', '.js']
  },
  devServer: {
    contentBase: './backend',
    hot: true,
    watchOptions: {
      aggregateTimeout: 300,
      poll: 100
    }
  }
}
