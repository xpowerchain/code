let schedule 		= require('node-schedule');
let transaction 	= require('./libs/transaction_sync.js');
let log4js          = require('./log4js_config.js');

const logger = log4js.log('cheese');

let rule_second_11 = new schedule.RecurrenceRule();
rule_second_11.second = [49]; //每1分钟
let second_11 = schedule.scheduleJob(rule_second_11, function(){
    console.log("schedule 1 minute >> transaction.all ...");
    logger.debug("schedule 1 minute >> transaction.all ...");
    transaction.all();
});
