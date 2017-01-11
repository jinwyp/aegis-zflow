/**
 * Created by JinWYP on 23/12/2016.
 */



var path = require("path");
module.exports = {
    entry: {
        nouse: "./js/nouse.js"
    },
    output: {
        path: path.resolve(__dirname, "build"),
        publicPath: "/",
        filename: "nouse.bundle.js"
    },

    devServer: {
        inline: true, //可以监控js变化
        hot: false, //热启动 Hot Module Replacement
        contentBase: path.join(__dirname), //制定静态文件目录
        publicPath: "/static/admin/js/",
        compress: true,
        port: 8020,  //默认8080
        proxy: {
            "/api": "http://192.168.1.118:9000"
        }
    }
};

