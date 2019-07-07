let async           = require('async');
let http            = require('http');
let fs              = require('fs');
let conf            = require('../conf.js');
let query           = require('../mysql_pool.js');
const queryPromise  = require('../mysql_pool_promise.js');
var log4js          = require('../log4js_config.js');
let dUtil           = require("./date_util.js");

const logger = log4js.log('cheese');


/*let mqttOptions = {
    host: conf.MQTT_HOST,
    port: conf.MQTT_PASSWORD,
    username: conf.MQTT_USERNAME,
    password: conf.MQTT_PASSWORD,
    protocol: conf.MQTT_PROTOCOL
}*/

let apiKey = 'KZYHRDG8X27K75WZFKN3M5T8YW365NPB53';

function timestampToTime(timestamp) {
    var date = new Date(timestamp * 1000);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    Y = date.getFullYear() + '-';
    M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    D = date.getDate() + ' ';
    h = date.getHours() + ':';
    m = date.getMinutes() + ':';
    s = date.getSeconds();
    return Y + M + D + h + m + s;
}

exports.all = function () {
    console.log(dUtil() + ' >> ' + 'transaction sync all begin....');
    if (fs.existsSync('transaction_sync_all.lock')) {
        console.log(dUtil() + ' >> ' + 'transaction_sync_all.lock exits');
        return;
    }
    fs.open('transaction_sync_all.lock', 'w', function (err, fd) {
    });
    let task = [];
    query("select address from addresses", [], function (err, address) {
        console.log(dUtil() + ' >> ' + 'select address...');
        if (err) {
            console.log(dUtil() + ' >> ' + 'select address error, err:..'+JSON.stringify(err));
            if (fs.existsSync('transaction_sync_all.lock')) {
                fs.unlink('transaction_sync_all.lock', function () {
                })
            }
            return;
        }
        address.forEach(function (e) {
            task.push(e.address);
        });
        async.eachSeries(task, function (item, callback) {
            setTimeout(function () {
                // console.log(dUtil() + ' >> ' + dUtil() + ' >> ' + item);
                http.get('http://api.etherscan.io/api?module=account&action=txlist&address=' + item + '&startblock=0&endblock=999999999&sort=desc&apiKey=' + apiKey,
                    function (res) {
                        let body = "";
                        res.on('data', function (chunk) {
                            body += chunk;
                        });
                        res.on('end', function () {
                            jsonObj = JSON.parse(body);
                            //console.log(dUtil() + ' >> ' + dUtil() + ' >> ' + 'http body: ' + body);
                            if (jsonObj.status === '1') {//只有有效的地址才可以返回
                                jsonObj.result.forEach(function (e) {
                                    if (e.input === '0x' && e.to.length > 0 && e.contractAddress.length === 0) {
                                        // eth转账
                                        //hash不在库中，插入
                                        query('select * from transactions where trans_hash=? and from_addr=? and to_addr=?', [e.hash,e.from,e.to], function (err, vals) {
                                            if (!err && vals.length === 0) {
                                                console.log(dUtil() + ' >> ' + 'hash not exist:' + e.hash);
                                                // need persistence
                                                // txHashs.push(e.hash);

                                                persistenceTx(e);

                                            }
                                        })
                                    }

                                })
                            }
                        });

                    }).on('error', function (e) {
                        console.log(dUtil() + ' >> ' + dUtil() + ' >> ' + 'error when http request, e: '+ e.message);
                        //callback(e);
                    });

                callback();
            }, 1000);

        }, function (err) {
            console.log(dUtil() + ' >> ' + '-----------transaction all err:' + JSON.stringify(err));
            if (fs.existsSync('transaction_sync_all.lock')) {
                fs.unlink('transaction_sync_all.lock', function () {
                })
            }
        })
    });
}

async function persistenceTx(jsonObj) {
    console.log(dUtil() + ' >> ' +  'being.......................');
    let selectSql               = 'select * from tokens where token_addr="' + jsonObj.contractAddress + '" limit 1';
    let dataList                = await queryPromise(selectSql);
    let symbol                  = '';
    let tokenName               = '';
    let tokenDecimal            = 0;
    if (dataList.length > 0) {
        symbol          = dataList[0].token_symbol;
        tokenName       = dataList[0].token_name;
        tokenDecimal    = dataList[0].decimals;
    }
    let insertSql = 'INSERT INTO transactions (trans_hash,from_addr, to_addr, token_addr, trans_time, block_number, block_hash, token_symbol, token_name, token_decimal, amount, gas, gas_price, gas_used,trans_status) VALUES ('
        + "'"+jsonObj.hash+"','"+jsonObj.from+"','"+jsonObj.to+"','"+jsonObj.contractAddress+"','"+timestampToTime(jsonObj.timeStamp)+"','"+jsonObj.blockNumber+"','"+jsonObj.hash+"','"+symbol
        +"','"+tokenName+"','"+tokenDecimal+"','"+jsonObj.value+"','"+jsonObj.gas+"','"+jsonObj.gasPrice+"','"+jsonObj.gasUsed+"','"+jsonObj.isError+"')  on duplicate key update trans_hash = '"+jsonObj.hash+"'";
    let insertResult = await queryPromise(insertSql);
    console.log(dUtil() + ' >> ' + 'end............................');
}

exports.tokentx = function () {
    if (fs.existsSync('transaction_sync_tokentx.lock')) {
        console.log(dUtil() + ' >> ' + 'ransaction_sync_tokentx.lock');
        return;
    }
    fs.open('transaction_sync_tokentx.lock', 'w', function (err, fd) {
    });
    let task = [];
    let body = '';
    console.log(dUtil() + ' >> '  + 'transaction_sync_tokentx start');
    query("select address from addresses", [], function (err, address) {
        if (err) {
            console.log(dUtil() + ' >> ' + 'database address error');
            if (fs.existsSync('transaction_sync_tokentx.lock')) {
                fs.unlink('transaction_sync_tokentx.lock', function () {
                });
            }
            return;
        };
        address.forEach(function (e) {
            task.push(e.address);
        });
        async.eachSeries(task, function (item, callback) {
            console.log(dUtil() + ' >> ' + 'tokentx:'+item);
            http.get('http://api.etherscan.io/api?module=account&action=tokentx&address=' + item + '&startblock=0&endblock=999999999&sort=desc&apiKey=' + apiKey, function (res) {
                let body = "";
                res.on('data', function (chunk) {
                    body += chunk;
                });
                res.on('end', function () {
                    console.log(dUtil() + ' >> ' + 'exports.tokentx >> url::' + 'http://api.etherscan.io/api?module=account&action=tokentx&address=' + item + '&startblock=0&endblock=999999999&sort=desc&apiKey=' + apiKey);
                    jsonObj = JSON.parse(body);
                    //console.log(dUtil() + ' >> ' + dUtil() + ' >> ' + body);
                    if (jsonObj.status === '1') {//只有有效的地址才可以返回
                        console.log(dUtil() + ' >> ' + 'exports.tokentx >> 11111111111111');
                        jsonObj.result.forEach(function (e) {
                            //hash不在库中，插入
                            query('select * from transactions where trans_hash=? and from_addr=? and to_addr=?', [e.hash,e.from,e.to], function (err, vals) {
                                if (vals.length === 0) {
                                    console.log(dUtil() + ' >> ' + 'hash not exist:' + e.hash);
                                    e['isError'] = 2;
                                    persistenceTx(e);
                                }
                            })
                        })
                    }
                });
                setTimeout(function () {
                    callback(null);
                }, 1000);
            }).on('error', function (e) {
                console.log(dUtil() + ' >> ' + 'exports.tokentx >> error:' + JSON.stringify(e))
                callback(e);
            });
        }, function (err) {
            console.log(dUtil() + ' >> ' + 'exports.tokentx >> err:' + JSON.stringify(err));
            if (fs.existsSync('transaction_sync_tokentx.lock')) {
                fs.unlink('transaction_sync_tokentx.lock', function () {
                })
            }
        })
    });
}
module.exports = exports;
//exports.tokentx();