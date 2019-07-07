let https  	 = require('https');
let fs		 = require('fs');
var log4js   = require('../log4js_config.js');
let dUtil           = require("./date_util.js");

const logger = log4js.log('cheese');
/**
 * 取得当前链上的gas price
 */
exports.gasPrice = function getGasprice(){
    let url = 'https://www.etherchain.org/api/gasPriceOracle';
    https.get(url, function (response){
        const { statusCode } = response;
        const contentType = response.headers['content-type'];
        let error;
        if (statusCode !== 200) {
            error = new Error('Request Failed.\n' +
                              `Status Code: ${statusCode}`);
        }
        if (error) {
            console.error(error.message);
            response.resume();
            return;
        }
        let body = '';
        // 当client接收到server的响应消息时会触发（会多次触发）
        response.on('data', function (data){
            body += data;
        });

        response.on('end', function (){
            let priceJson = JSON.stringify(body);
            fs.writeFileSync('gas_price.json', priceJson);
        });
    }).on('error', function (err) {
        console.error(dUtil() + ' >> 从etherchain取gasprice时异常');
        logger.error(dUtil() + ' >> 从etherchain取gasprice时异常');
    });
}

/**
 * 取得gas的估算值
 */
exports.estimateGas = function estimateGas (amount, toAddress, tokenAddress, tokenerc20Contract, web3) {
    let data = tokenerc20Contract.transfer.getData(toAddress, amount);
    var result = web3.eth.estimateGas({
        to: tokenAddress,
        data: data,
    });
    console.log('result:::'+result);
    return result;
}

module.exports = exports;