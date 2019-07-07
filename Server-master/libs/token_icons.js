let query = require('../mysql_pool.js');
let https = require('https');
let fs    = require('fs');
let cheerio = require('cheerio');
let sql = 'select token_addr from tokens where token_addr not in (SELECT token_addr FROM token_icons group by token_addr) group by token_addr';
let apiKey = 'KZYHRDG8X27K75WZFKN3M5T8YW365NPB53'
exports.icons = function () {
    if(fs.existsSync('token_icons.lock')){
        return;
    };
    fs.open('token_icons.lock','w',function (err,fd) {
        //fs.close(fd);
    });
    query(sql,[],function (err,vals) {
        if(vals){
            vals.forEach(function (item) {
                https.get('https://etherscan.io/token/'+item.token_addr, function (re) {
                    let body = "";
                    re.on('data', function (chunk) {
                        body += chunk;
                    });
                    re.on('end', function (re) {
                        let $ = cheerio.load(body);
                        let url = $('h1.pull-left').children('img').attr('data-cfsrc');
                        if(typeof (url)!='undefined'){
                            url = 'https://etherscan.io'+url;
                        }else{
                            url = 'http://ad.fshd.com/uploads/image/2018/08/10/default.png';
                        }
                        query('INSERT INTO `token_icons` (`token_addr`, `icon_url`) VALUES (?,?);',[item.token_addr,url],function (err,vals) {

                        })
                    })
                })
            })
        }
        if(fs.existsSync('token_icons.lock')){
            fs.unlink('token_icons.lock',function () {
                
            });
        }
    })
}
exports.abi = function(){
    if(fs.existsSync('token_abi.lock')){
        return;
    };
    fs.open('token_abi.lock','w',function (err,fd) {
        //fs.close(fd);
    });
    query('select token_addr from token_icons where abi is null',[],function (err,vals) {
        if(!err&&vals.length>0){
            vals.forEach(function (item) {
                https.get('https://api.etherscan.io/api?module=contract&action=getabi&address='+item.token_addr+'&apikey='+apiKey, function (re) {
                    let body = "";
                    re.on('data', function (chunk) {
                        body += chunk;
                    });
                    re.on('end', function (re) {
                        jsonObj = JSON.parse(body);
                        if (jsonObj.status==='1') {
                            query('update `token_icons` set abi=? where token_addr=?;',[jsonObj.result,item.token_addr],function (err,vals) {

                            })
                        }
                    })
                })
            })
        }
        if(fs.existsSync('token_abi.lock')){
            fs.unlink('token_abi.lock',function () {

            })
        }
    })
}
//0xaa0bb10cec1fa372eb3abc17c933fc6ba863dd9e
module.exports = exports;
