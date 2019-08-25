// rollup.config.js
import globals from 'rollup-plugin-node-globals';
import builtins from 'rollup-plugin-node-builtins';
import resolve from 'rollup-plugin-node-resolve';
import commonjs from 'rollup-plugin-commonjs';

export default {
  input: 'src/js/index.js',
  output: {
    file: 'dist/index.js',
    format: 'umd',
    name: 'RollupModule',
  },
  plugins: [
    globals(),
    builtins(),
    resolve({
      preferBuiltins: true
    }),
    commonjs()
  ]
};
