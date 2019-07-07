let schedule 		= require('node-schedule');
let tokenIcons 		= require('./libs/token_icons.js');
var log4js          = require('./log4js_config.js');

const logger = log4js.log('cheese');

let rule3 = new schedule.RecurrenceRule();
rule3.minute = [1,6,11,16,21,26,31,36,41,46,51,56];//每5分钟

let sche3 = schedule.scheduleJob(rule3, function(){

    console.log("schedule 5 minutes...");
    tokenIcons.abi();
    tokenIcons.icons();
});