const shelljs = require("shelljs");
//const pwd = require("shelljs/src/pwd");
const yargs = require("yargs");

// console.log(pwd);
// console.log(shelljs);
// console.log(yargs);
// console.log(pwd());

const {main} = require("../../build/lib.js");
main(shelljs, yargs);
