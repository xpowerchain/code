let schedule 		= require('node-schedule');
let query 			= require('./mysql_pool.js');
let http 			= require('http');
let fs              = require('fs')
let async 			= require('async');
let transaction 	= require('./libs/transaction_sync.js');
let log4js          = require('./log4js_config.js');
let dUtil           = require("./libs/date_util");
let math            = require('mathjs');
let ethPlorerApiKey = 'freekey';

const logger = log4js.log('cheese');

Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "H+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

// 处理ethplorer返回的token列表数据
let httpCallback = function (response){
	const { statusCode } = response;
	let error;
	if (statusCode !== 200) {
	    error = new Error('Request Failed.\n' +
	                      `Status Code: ${statusCode}`);
	}
    if (error) {
    	console.error(dUtil() + ' >> ' + error.message);
    	response.resume();
    	return;
    }
	let body = '';
	response.on('data', function (data){
		body += data;
	});
	response.on('end', function (){
		console.log(dUtil() + ' >> 数据接收完成。');
		logger.debug(dUtil() + ' >> get token list schedule 数据接收完成。');
		let tokenJson = JSON.parse(body);
		let walletAddr    = tokenJson.address;
        if (tokenJson.tokens) {
            query('select id from addresses where address = ?', [walletAddr], function (err,vals){
                if (!err) {
                    let time2 = new Date().Format("yyyy-MM-dd HH:mm:ss");
                    let address_id = vals[0].id;

                    query('INSERT INTO `token_server`.`tokens` (`address_id`, `token_name`, `token_num`, `token_addr`, `token_symbol`, `decimals`, `rate`) VALUES (?,?,?,?,?,?,?) on duplicate key update token_num=?, create_time = ?',
                        [address_id,'ETH',tokenJson['ETH']['balance']*1000000000000000000,'0x','ETH',18,0,tokenJson['ETH']['balance']*1000000000000000000,time2],function (err,vals) {
                            if (err) {
                                console.log('-------err:'+JSON.stringify(err));
                            }
                            tokenJson.tokens.forEach(
                                function (item) {
                                    let sql 			= 'INSERT INTO `token_server`.`tokens` (`address_id`, `token_name`, `token_num`, `token_addr`, `token_symbol`, `decimals`, `rate`) VALUES (?, ?, ?, ?, ?, ?, ?) on duplicate key update token_num=?, create_time = ?';
                                    let token_name 		= item.tokenInfo.name;
                                    let decimals		= item.tokenInfo.decimals;
                                    let token_addr  	= item.tokenInfo.address;
                                    let token_symbol 	= item.tokenInfo.symbol;
                                    let token_num       = math.format(item.balance,{"lowerExp":0,"upperExp":Infinity});
                                    let rate			= typeof(item.tokenInfo.price.rate) != 'undefined' ? item.tokenInfo.price.rate : 0;

                                    console.log(dUtil() + ' >> address_id:'+address_id+", token_name:" + token_name+",token_num:"+token_num+",token_addr:"
                                        +token_addr+",token_symbol:"+token_symbol+",decimals:"+decimals+",rate:"+rate+",now:"+Date.now());
                                    logger.debug(dUtil() + ' >> address_id:'+address_id+", token_name:" + token_name+",token_num:"+token_num+",token_addr:"
                                        +token_addr+",token_symbol:"+token_symbol+",decimals:"+decimals+",rate:"+rate+",now:"+Date.now());

                                    query(sql, [address_id, token_name, token_num, token_addr, token_symbol, decimals, rate, token_num, time2], function (err, innerVals) {
                                        if (err) {
                                            console.error(dUtil() + ' >> 保存token时异常：'+err);
                                            logger.error(dUtil() + ' >> 保存token时异常：'+err);
                                            return;
                                        }
                                    });
                                }
                            );
                    });


                } else {
                    console.error(dUtil() + ' >> ' + err);
                    logger.error(dUtil() + ' >> err:' + err);
                    return;
                }
            });
        }
	});
};

let urls = [];
let rule = new schedule.RecurrenceRule();
rule.second = [45]; //每隔1分钟执行一次
schedule.scheduleJob(rule, function(){
    console.log(dUtil() + ' >> ' + "schedule1...");
    logger.info(dUtil() + ' >> token list schedule...');
    if(fs.existsSync('token_list.lock')){
    	console.log(dUtil() + ' >> ' + 'token_list.lock')
        return;
    };
    fs.open('token_list.lock','w',function (err,fd) {
    });
	
	query('select address from addresses',[], function(err,vals){
		if (!err&&vals.length>0){
			vals.forEach(
				function (value,index,array) {
					if (value.address.length>40) {
						urls.push({url:'http://api.ethplorer.io/getAddressInfo/'+value.address+"?apiKey="+ethPlorerApiKey, delay: 1500});
					}
				}
			);
			async.eachSeries(urls, function(item, callback){
				console.log(dUtil() + ' >> ' + 'current url: ' + item.url);
		        logger.debug(dUtil() + ' >> get token list, curent url:'+item.url);
		        http.get(item.url, httpCallback).on('error',(e) => {
		        		logger.error(dUtil() + ' >> ' + `while send request got error: ${e.message}`);
						
					});
				setTimeout(function () {
                        callback(null);
                    }, 5000);

			}, function (err) {
				console.log('########err::'+JSON.stringify(err));
                if(fs.existsSync('token_list.lock')){
                    fs.unlink('token_list.lock',function () {
                    	console.log(dUtil() + ' >> ' + 'unlink token_list.lock')
                    })
                }
			});
		}else{
			console.log(dUtil() + ' >> ' + err);
            if(fs.existsSync('token_list.lock')){
                fs.unlink('token_list.lock',function () {
                    console.log(dUtil() + ' >> ' + 'unlink token_list.lock')
                })
            }
		}
	});
});



let rule_second_6 = new schedule.RecurrenceRule();
rule_second_6.minute = [1,6,11,16,21,26,31,36,41,46,51,56]; //5分钟
schedule.scheduleJob(rule_second_6, function(){
    console.log(dUtil() + " >> schedule 20 seconds...");
    logger.debug(dUtil() + ' >> schedule 20 seconds...');
    transaction.tokentx();
});
