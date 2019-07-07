let schedule 		= require('node-schedule');
let balance 		= require('./libs/balance_sync.js');
let log4js          = require('./log4js_config.js');

const logger = log4js.log('cheese');

let rule_second_6 = new schedule.RecurrenceRule();
rule_second_6.second = [19,39,59]; //每20秒
let second_6 = schedule.scheduleJob(rule_second_6, function(){

    console.log("schedule balance seconds...");
    logger.debug(Date() + " schedule >> balance seconds...");
    balance();
});