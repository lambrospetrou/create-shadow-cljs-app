const webpack = require("webpack");
const path = require('path');

module.exports = {
  target: 'node',
  node: {
    __dirname: false
  },
  plugins: [
    new webpack.BannerPlugin({ banner: "#!/usr/bin/env node", raw: true }),
    new webpack.DefinePlugin({
      $dirname: '__dirname',
    })
  ],
  //mode: 'production',
  mode: 'development',
  entry: './src/js/index.js',
  output: {
    filename: 'index.js',
    path: path.resolve(__dirname, 'dist')
  }
};
