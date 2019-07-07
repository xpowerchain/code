let query 			= require('../mysql_pool.js');
let log4js          = require('../log4js_config.js');
let async 			= require('async');
let https 			= require('https');
let fs 				= require('fs');

let apikey = 'KZYHRDG8X27K75WZFKN3M5T8YW365NPB53';
let urls = [];

const logger = log4js.log('cheese');

exports.updateTxStatus = function updateTxStatus(){

	if(fs.existsSync('update_tx_status.lock')){
        return;
    };
    fs.open('update_tx_status.lock','w',function (err,fd) {
        //fs.close(fd);
    });

	let sql = 'select * from transactions where trans_status=2';
	query(sql, null, function (err, vals) {
		if (!err && vals.length > 0) {
			console.log('valslength::: '+vals.length);
			vals.forEach(function(item){
				let url = 'https://api.etherscan.io/api?module=transaction&action=getstatus&txhash='+item.trans_hash+'&apikey='+apikey;
				urls.push(JSON.stringify({'url':url,'trans_hash':item.trans_hash}));
			});
			async.eachSeries(urls,function(item,callback){
				console.log('updateTxStatus >> url::'+item.url)
				logger.debug('updateTxStatus >> url::'+item.url)
				https.get(item.url, function (res) {
					let body = '';
					res.on('data', function (chunk){
						body+=chunk;
					});
					res.on('end',function(){
						jsonObj = JSON.parse(body);
						if (jsonObj.status === '1') {
							let upSql = 'update transactions set trans_status = ? where trans_hash="' + item.trans_hash + '"';
							query(upSql, [jsonObj.result.isError], function(err, vals) {
								console.log('updateTxStatus >> err::'+JSON.stringify(err));
								console.log('updateTxStatus >> update success');
								logger.debug('updateTxStatus >> update success');
							})
						}
					});
					setTimeout(function () {
	                    callback(null);
	                }, 500);
				}).on('error', function (e) {
	                callback(e);
	            });

			},function(err){
					            if(fs.existsSync('update_tx_status.lock')){
					                fs.unlink('update_tx_status.lock',function () {})
					            }
			        		});
		} else {
			if(fs.existsSync('update_tx_status.lock')){
                fs.unlink('update_tx_status.lock',function () {})
            }
		}
	});
}
//exports.updateTxStatus();
module.exports = exports
