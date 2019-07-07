let Web3 = require('web3')
let fs = require('fs');
let conf  = require('../conf.js');
let query = require('../mysql_pool.js');
let math = require('mathjs');
var log4js    = require('../log4js_config.js');

const logger = log4js.log('cheese');
// 部署合约实例
// 获取ABI
let func = function () {
    if(fs.existsSync('balance_sync.lock')){
        return;
    };
    fs.open('balance_sync.lock','w',function (err,fd) {
        //fs.close(fd);
    });
    function choosePeer(){
        let url = "";
        if(fs.existsSync('node_server.conf')){
            url = fs.readFileSync('node_server.conf').toString();
        }else{
            url = conf.RPC_HOSTS[0];
        }
        provider = new Web3.providers.HttpProvider(url);
        return new Web3(provider);
    }
    let web3 = choosePeer();
    let abis = {};
    query('select * from token_icons',[],function (err,vals) {
        if (vals) {
            vals.forEach(function (item) {
                abis[item['token_addr']] = item['abi'];
            })
            query('select t.*,a.address from tokens t left join addresses a on t.address_id=a.id',[],function (err,vals) {
                if(vals.length>0){
                    let tokenerc20Contract = '';
                    vals.forEach(function (item) {
                        let balance = 0;
                        if(typeof (abis[item.token_addr]) == 'undefined'){//icon未同步
                            console.log('icon未同步:'+item.token_addr);
                            return;
                        }else if(item['token_name']=='ETH'){//eth
                            console.log('ETH:'+item.address);
                            balance = web3.eth.getBalance(item.address).toString();
                            console.log(balance);
                        }else if(abis[item['token_addr']]===null){
                           console.log('abi不存在：'+item['token_addr'])
                            return;
                        }else{
                            tokenerc20Contract = web3.eth.contract(JSON.parse(abis[item.token_addr])).at(item.token_addr);

                            balance = math.format(Number(tokenerc20Contract.balanceOf.call(item.address).toString()),{"lowerExp":0,"upperExp":Infinity})
                            console.log('balance_sync>>balance, address:'+item.address+', token addr:'+item.token_addr+',balance::'+balance);
                        }
                        query('update tokens set token_num=? where id=?',[balance,item['id']],function (err,vals) {
                        })
                    });
                }
                if(fs.existsSync('balance_sync.lock')){
                    fs.unlink('balance_sync.lock',function () {
                        console.log('balance unlock')
                    })
                }
            })
        }
        
    });
};

module.exports = func;
//func();
