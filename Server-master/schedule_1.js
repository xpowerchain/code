let schedule 		= require('node-schedule');
let gasPrice 		= require('./libs/gas.js');
let updateTxStatus  = require('./libs/update_txs_stas.js');
var log4js          = require('./log4js_config.js');
let dUtil           = require("./libs/date_util");

const logger = log4js.log('cheese');

let rule2 = new schedule.RecurrenceRule();
rule2.second = [1,31]; //每30秒
let sche2 = schedule.scheduleJob(rule2, function(){

    console.log(dUtil() + " >> schedule 30 seconds...");
    logger.debug(dUtil() + ' >> schedule 30 seconds...');
    gasPrice.gasPrice();
    updateTxStatus.updateTxStatus();
});