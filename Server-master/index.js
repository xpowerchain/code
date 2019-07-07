/**
 * fshd 钱包服务端
 * webjs版本： 0.20.6  即安装: npm install web3@v0.20.6
 */
var Web3                = require('web3')
var fs                  = require('fs')
var ethereumjsWallet    = require('ethereumjs-wallet'); //引入以太坊nodejs操作钱包支持
var Tx                  = require("ethereumjs-tx"); //引入以太坊js交易支持
var express             = require('express');
var bodyParser          = require('body-parser');
var query               = require('./mysql_pool.js');
var conf                = require('./conf.js');
var sign                = require('./libs/sign.js');
var log4js              = require('./log4js_config.js');
var http                = require('http');
var https               = require('https');
var app                 = express();
let bii                 = require('./libs/base_info_input.js');
let moment              = require('moment');

//create http https server
var privateKey          = fs.readFileSync('./crt/private.pem', 'utf8');
var certificate         = fs.readFileSync('./crt/file.crt', 'utf8');
var credentials         = {key: privateKey, cert: certificate};
var httpServer          = http.createServer(app);
var httpsServer         = https.createServer(credentials, app);

// httpServer.listen(8081, function() {
//     console.log('HTTP Server is running on: http://localhost:%s', 8081);
// });
httpsServer.listen(8082, function() {
    console.log('HTTPS Server is running on: https://localhost:%s', 8082);
});

app.use(bodyParser.urlencoded({ extended: false }));
const logger = log4js.log('cheese');

function test(req, res) {
    let web3 = new Web3(new Web3.providers.HttpProvider("http://39.107.158.137:8546"));
    let keystore = '{"address":"157b031c3f762e4c2316adf7161940c5bbef335e","crypto":{"cipher":"aes-128-ctr","cipherparams":{"iv":"9caeb68a059a0b838b220cb6b9c629d0"},"ciphertext":"88abed62fb5115678ee3bb62f9accd01d1222237fd01bba1ffac12ad0eff18b4","kdf":"scrypt","kdfparams":{"dklen":32,"n":65536,"p":1,"r":8,"salt":"14a764b4de3e6a4dc8ba1185b262e463b66d713c9b77e59ce4fc8c68c2066e9f"},"mac":"15c8eec780c8b5e95bd2bb9ef5c738cc5d45197f8f7745396766f8e95351e673"},"id":"6a23d9a6-1b66-4138-a33a-07d1ca30a6ea","version":3}';
    let pass = 'Lmkuangxing1';
    let wallet = ethereumjsWallet.fromV3(keystore,pass);
    let privateKey = wallet.getPrivateKey();

    let fromAddress = wallet.getAddress().toString('hex');
    let toAddress = '0x157b031c3f762e4c2316adf7161940c5bbef335e';

    let fromAddressBalance = web3.fromWei(web3.eth.getBalance(fromAddress),'ether');
    console.info("-------address:"+fromAddressBalance)
    let number = web3.eth.getTransactionCount("0x"+fromAddress);
    console.log('number:'+number);
    let rawTx = {
        nonce:web3.toHex(number),
        gasPrice: web3.toHex(90000000000),
        gasLimit:web3.toHex(900000),
        to:toAddress,
        // value:web3.toHex(web3.toWei(1,'ether')),
        value: 1000000000,
        data: ''
    };
    console.log('rawTx:'+rawTx);
    let tx = new Tx(rawTx);
    tx.sign(privateKey);
    console.log("tx:"+tx)
    let serializedTx = tx.serialize();
    web3.eth.sendRawTransaction('0x'+serializedTx.toString('hex'),function(err,hash) {
        console.log('transaction id: ' + hash);
        console.log(err);
    })
};

function choosePeer(){
    let url = "";
    if(!fs.existsSync('tmp/node_server.conf')){
         url = fs.readFileSync('tmp/node_server.conf').toString();
    }else{
         url = conf.RPC_HOSTS[0];
    }
    provider = new Web3.providers.HttpProvider(url);
    return new Web3(provider);
}

let web3 = choosePeer();

/********************开始请求操作************************/

app.get('/txs', function(req, res) {
    test();
});

app.post('/test',function(req,res){

    //sign(req.body);return;

    console.log(req.body);
    return 1;
    bii(req.body);
    res.end(res.body)
    return;
    console.log(req.body);
    const ordered = req.body;
    let unordered = ordered;
    Object.keys(unordered).sort().forEach(function(key) {
        ordered[key] = unordered[key];
    });

    console.log(JSON.stringify(ordered));
    res.end(res.body)
})

// 获取ABI
var abi = [
    {
        "constant": true,
        "inputs": [],
        "name": "name",
        "outputs": [
            {
                "name": "",
                "type": "string"
            }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
    },
    {
        "constant": true,
        "inputs": [],
        "name": "totalSupply",
        "outputs": [
            {
                "name": "",
                "type": "uint256"
            }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
    },
    {
        "constant": true,
        "inputs": [],
        "name": "decimals",
        "outputs": [
            {
                "name": "",
                "type": "uint8"
            }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
    },
    {
        "constant": true,
        "inputs": [
            {
                "name": "",
                "type": "address"
            }
        ],
        "name": "balanceOf",
        "outputs": [
            {
                "name": "",
                "type": "uint256"
            }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
    },
    {
        "constant": true,
        "inputs": [],
        "name": "symbol",
        "outputs": [
            {
                "name": "",
                "type": "string"
            }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
    },
    {
        "constant": true,
        "inputs": [
            {
                "name": "",
                "type": "address"
            },
            {
                "name": "",
                "type": "address"
            }
        ],
        "name": "allowance",
        "outputs": [
            {
                "name": "",
                "type": "uint256"
            }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
    },
    {
        "inputs": [
            {
                "name": "initialSupply",
                "type": "uint256"
            },
            {
                "name": "tokenName",
                "type": "string"
            },
            {
                "name": "tokenSymbol",
                "type": "string"
            }
        ],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "constructor"
    },
    {
        "anonymous": false,
        "inputs": [
            {
                "indexed": true,
                "name": "from",
                "type": "address"
            },
            {
                "indexed": true,
                "name": "to",
                "type": "address"
            },
            {
                "indexed": false,
                "name": "value",
                "type": "uint256"
            }
        ],
        "name": "Transfer",
        "type": "event"
    },
    {
        "anonymous": false,
        "inputs": [
            {
                "indexed": true,
                "name": "_owner",
                "type": "address"
            },
            {
                "indexed": true,
                "name": "_spender",
                "type": "address"
            },
            {
                "indexed": false,
                "name": "_value",
                "type": "uint256"
            }
        ],
        "name": "Approval",
        "type": "event"
    },
    {
        "anonymous": false,
        "inputs": [
            {
                "indexed": true,
                "name": "from",
                "type": "address"
            },
            {
                "indexed": false,
                "name": "value",
                "type": "uint256"
            }
        ],
        "name": "Burn",
        "type": "event"
    },
    {
        "constant": false,
        "inputs": [
            {
                "name": "_to",
                "type": "address"
            },
            {
                "name": "_value",
                "type": "uint256"
            }
        ],
        "name": "transfer",
        "outputs": [
            {
                "name": "success",
                "type": "bool"
            }
        ],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
    },
    {
        "constant": false,
        "inputs": [
            {
                "name": "_from",
                "type": "address"
            },
            {
                "name": "_to",
                "type": "address"
            },
            {
                "name": "_value",
                "type": "uint256"
            }
        ],
        "name": "transferFrom",
        "outputs": [
            {
                "name": "success",
                "type": "bool"
            }
        ],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
    },
    {
        "constant": false,
        "inputs": [
            {
                "name": "_spender",
                "type": "address"
            },
            {
                "name": "_value",
                "type": "uint256"
            }
        ],
        "name": "approve",
        "outputs": [
            {
                "name": "success",
                "type": "bool"
            }
        ],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
    },
    {
        "constant": false,
        "inputs": [
            {
                "name": "_spender",
                "type": "address"
            },
            {
                "name": "_value",
                "type": "uint256"
            },
            {
                "name": "_extraData",
                "type": "bytes"
            }
        ],
        "name": "approveAndCall",
        "outputs": [
            {
                "name": "success",
                "type": "bool"
            }
        ],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
    },
    {
        "constant": false,
        "inputs": [
            {
                "name": "_value",
                "type": "uint256"
            }
        ],
        "name": "burn",
        "outputs": [
            {
                "name": "success",
                "type": "bool"
            }
        ],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
    },
    {
        "constant": false,
        "inputs": [
            {
                "name": "_from",
                "type": "address"
            },
            {
                "name": "_value",
                "type": "uint256"
            }
        ],
        "name": "burnFrom",
        "outputs": [
            {
                "name": "success",
                "type": "bool"
            }
        ],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
    }
]
// 代币合约地址
var tokenAddress = conf.TOKEN_ADDRESS;
// 部署合约实例
var tokenerc20Contract = web3.eth.contract(abi).at(tokenAddress);

// keystore 文本串
var keystore = '{"address":"9cf99ffaf7c25229634ed90c39bde95f0c3a97bf","crypto":{"cipher":"aes-128-ctr","ciphertext":"3a0b7c3af0ff51cc3e4cefb4fd07a226d4f4828e8995a479e55a179759fe1f9b","cipherparams":{"iv":"8347e34fe376c1e736c17d6df6406aea"},"kdf":"scrypt","kdfparams":{"dklen":32,"n":262144,"p":1,"r":8,"salt":"75585a2ef9ddad7576d71723839b7b888bfc70fa43224bb90667871af977934e"},"mac":"992038fd1c7584848031ce910a354c7ac1ec0b73c64fd85338aaf8fa09234b94"},"id":"88144ff3-200c-433a-bb50-4d0a198e9763","version":3}';
// 账户密码
var pass = "cfsjdDD0110";
//var to = "0xcbcd3f06cc3ace55fcd1bd784fe907444085dff2"; // 美锐用 
var to = "0xb2ccecb699f850de65a8a2dda5517879c3f97164"; // kevoo
var amount = "50000000000000000000000";
//sendRawTransaction(keystore,pass,to);

app.get('/sendRawTransaction', function (req,res){
    var txs = sendRawTransaction(keystore,pass,to,amount);
    res.send(txs);
});

// 不依赖节点keystore文件进行转账
function sendRawTransaction(keystore="",pass="",toAddress,amount) {
    let web3 = choosePeer();
    // 通过keystore与密码得到钱包对象
    var wallet = ethereumjsWallet.fromV3(keystore, pass);
    // 获取私钥
    var privateKey = wallet.getPrivateKey();

    var fromAddress = wallet.getAddress().toString("hex");
    console.log('tokenAddress:'+tokenAddress);
    console.log('fromAddress:'+fromAddress);
    let data = tokenerc20Contract.transfer.getData(toAddress, amount);
    // 返回指定地址发起的交易数
    var number = web3.eth.getTransactionCount("0x" + fromAddress);
    var gasPrice = web3.eth.gasPrice;
    var gasLimit = 210000;
    //通过交易参数
    var rawTx = {
        from: fromAddress,
        nonce: web3.toHex(number),//交易数
        gasPrice: web3.toHex(gasPrice),//gas价格
        gasLimit:  web3.toHex(gasLimit),//gas配额
        to: tokenAddress,//指出调用合约的地址
        value: '0x0',//以太币数量
        data: data
    };

    //构造此交易对象
    var tx = new Tx(rawTx);
    //发起人私钥签名
    tx.sign(privateKey);
    //交易序列化
    var serializedTx = tx.serialize();
    //执行交易
    console.log('----serializedTx:'+ '0x' + serializedTx.toString('hex'));
    web3.eth.sendRawTransaction('0x' + serializedTx.toString('hex'), function(err, hash) {
        if (!err) {
            var transaction = web3.eth.getTransaction(hash);
            console.log(transaction);
            return transaction;
        } else {
            console.log(err);
        }
    });
}

// 循环遍历代币钱包余额
app.get('/getBalance',function(req, res) {
    let web3 = choosePeer();
var data = {};
web3.eth.accounts.forEach(address => {
 tokens = tokenerc20Contract.balanceOf.call(address)
 //console.log(tokens);
 data[address] = tokens;
})
// 输出 JSON 格式
 var response = {
   "code":200,
     "data":data
 };

res.end(JSON.stringify(response));
})

// 查询代币余额
app.get('/getBalanceOf',function(req, res) {
  var address = req.query.address;
  // 输出 JSON 格式
    var response = {
      "code":200,
        "address":req.query.address,
        "balanceof":tokenerc20Contract.balanceOf.call(address)
    };

   res.end(JSON.stringify(response));
})

// 转账
app.get('/transfer',function(req, res) {
  var from = req.query.from!=undefined?req.query.from:web3.eth.accounts[0];
  var to = req.query.to!=undefined?req.query.to:web3.eth.accounts[1];
  var amount = req.query.amount!=undefined?req.query.amount:100;
  var passwd = "jioUU987&";

  console.log("from:"+from+",to:"+to+",amount:"+amount);

  var transactionHash = transfer(from, to, amount, passwd)
  // 输出 JSON 格式 
    var response = {
      "code":200,
        "from":tokenerc20Contract.balanceOf.call(from),
        "to":tokenerc20Contract.balanceOf.call(to),
        "amount":amount,
        "hash":transactionHash
    };

   res.end(JSON.stringify(response));
})

//transfer("0xd71a0cd38CAaa8e7dD94e9a4232FC665012e1Ae0", "0x96Fb2Af2e5261A572d1535131ba0c80c594bEE62", 100, "123456")
// 执行转账 (依赖节点keystore文件)
function transfer(from, to, amount, passwd) {
  // 获取合约小数位数
  var decimals = tokenerc20Contract.decimals.call().toString(10);
  // 转账金额100
  var amount = amount*Math.pow(10,decimals);

  // 解锁账户
  web3.personal.unlockAccount(from,passwd, 15000)
  console.log(to);
  // 执行转账
  transactionHash = tokenerc20Contract.transfer(to, amount, { from: from, gasPrice: 500000000000, gas:60000 })
  // 获取交易信息
  var transaction = web3.eth.getTransaction(transactionHash);
  console.log(transaction);
  return transactionHash;
}

/*var myEvent = tokenerc20Contract.Transfer({some: 'args'}, {fromBlock: 0, toBlock: 'latest'});
myEvent.watch(function(error, result){
    console.log("代币tx列表::" + JSON.stringify(result));
    query('select * from task_transaction where hash=?',[result.transactionHash],function (err,vals) {
        if(!vals||vals.length==0){
            query('INSERT INTO `task_transaction` (`hash`, `updated_at`,`address`) VALUES (?,?,?)',[result.transactionHash,parseInt(Date.now()/1000),result.address],function(err,vals){

            })
        }
    });
});*/

/**
 * 获取交易详情
 * tx：transaction_id
 */
app.post('/api/tx_info', function (req,res){
    let web3 = choosePeer();
    let apiKey = 'KZYHRDG8X27K75WZFKN3M5T8YW365NPB53';
    if(!sign(req.body)){
        res.send(JSON.stringify({'code':'103','msg':'签名错误','data':[]}));
        return ;
    }
    let tx = req.body.tx;
    if(!tx||tx.length!==66){
        return res.send(JSON.stringify({'code':'102','msg':'参数错误'}));
    }
    bii(req.body);
    let txObj = web3.eth.getTransaction(tx);
    if(txObj===null){
        https.get('https://api.etherscan.io/api?module=proxy&action=eth_getTransactionByHash&txhash=' + tx + '&apiKey=' + apiKey, function (req,re) {
            let body = "";
            req.on('data', function (chunk) {
                body += chunk;
            });
            req.on('end', function () {
                jsonObj = JSON.parse(body);
                if (jsonObj['result']) {
                    let transaction = jsonObj['result'];
                    https.get('https://api.etherscan.io/api?module=proxy&boolean=false&action=eth_getBlockByNumber&tag=' + transaction['blockNumber'] + '&apiKey=' + apiKey, function (req,re) {
                        let body = "";
                        req.on('data', function (chunk) {
                            body += chunk;
                        });
                        req.on('end', function () {
                            //console.log('https://api.etherscan.io/api?module=proxy&boolean=false&action=eth_getBlockByNumber&tag=' + transaction['blockNumber'] + '&apiKey=' + apiKey);
                            //console.log(body);
                            jsonObj = JSON.parse(body);
                            //console.log(re);
                            if (jsonObj['result']) {
                                let tmp= parseInt(jsonObj['result']['timestamp'],16);
                                jsonObj['result']['timestamp'] = moment(tmp*1000).format('YYYY-MM-DDTHH:mm:ss.000')+'Z';
                                return res.send(JSON.stringify({'code':'200','msg':'成功','data':{'transaction':transaction,'block':jsonObj['result']}}));
                            }else{
                                return res.send(JSON.stringify({'code':'101','msg':'无数据'}));
                            }
                        })
                    })
                }else{
                    return res.send(JSON.stringify({'code':'101','msg':'无数据'}));
                }
            })
        })
    }else{
        let block = web3.eth.getBlock(txObj.blockHash);
        let tmp = parseInt(block['timestamp'],16);
        block['timestamp'] = moment(tmp*1000).format('YYYY-MM-DDTHH:mm:ss.000')+'Z';
        return res.send(JSON.stringify({'code':'200','msg':'成功','data':{'transaction':txObj,'block':web3.eth.getBlock(txObj.blockHash)}}));
    }
});

/**
 * 生成离线交易要用到的data串
 */
app.post('/api/data_txs',function(req, req) {
    let web3 = choosePeer();
    if(!sign(req.body)){
        return res.send(JSON.stringify({'code':'103','msg':'签名错误','data':[]}));
    }
    let toAddress       = req.body.to_address;
    let tokenAddress    = req.body.token_address; 
    let amount          = req.body.amount;
    let brand           = req.body.brand;
    let type            = req.body.ptype;
    let deviceId        = req.body.device_id;
    let version         = req.body.sys_version;
    let appVersion      = req.body.app_version;
    console.log('data_txs>>tokenAddress:'+token_address+',toAddress:'+toAddress+',amount:'+amount+
        ',brand:'+brand+',type:'+type+',deviceId:'+deviceId+',version:'+version+',appVersion:'+appVersion);
    logger.info('data_txs>>tokenAddress:'+token_address+',toAddress:'+toAddress+',amount:'+amount+
        ',brand:'+brand+',type:'+type+',deviceId:'+deviceId+',version:'+version+',appVersion:'+appVersion);
    if (!toAddress || !tokenAddress || !amount || !brand || !type || 
            !deviceId || !version || !appVersion ||tokenAddress.length != 42) {
        return res.send(JSON.stringify({'code':'403','msg':'params error!'}));
    }
    bii(req.body);
    let tokenerc20Contract = web3.eth.contract(abi).at(tokenAddress);
    let data = tokenerc20Contract.transfer.getData(toAddress, amount);
    console.log('data_txs>>return tx data:'+data+', deviceId:'+deviceId);
    logger.info('data_txs>>return tx data:'+data+', deviceId:'+deviceId);
    return res.send(JSON.stringify({'code':'200','data':data}));
});

/**
 * 获取交易记录
 * addr:发起人地址
 */
app.post('/api/txs', function (req,res) {
    if(!sign(req.body)){
        res.send(JSON.stringify({'code':'103','msg':'签名错误','data':{}}));
        return ;
    }
    let addr = req.body.addr;
    let page = req.body.page?req.body.page:1;
    let symbol = req.body.symbol;
    let pageLimit = 10;
    if(!addr){
        return res.send(JSON.stringify({'code':'102','msg':'参数错误'}));
    }
    bii(req.body);
    let sql = "select * from transactions where (from_addr=? or to_addr=?)"
    let params = [addr,addr];

    if (symbol) {
        if (symbol == 'ETH') {
            sql += " and (token_symbol = '' or token_symbol = 'ETH')  order by trans_time desc limit ?,?"
        } else {
            sql += " and token_symbol = '"+symbol+"' order by trans_time desc limit ?,?"
        }
    } else {
        sql += " order by trans_time desc limit ?,?"
    }

    params.push(pageLimit*(page-1))
    params.push(pageLimit)

    console.log('sql::'+sql);

    query(sql,params,function (err,vals) {
        return res.send({'code':'200','msg':'成功','data':vals});
        console.log(err);
    })
});

/**
 * 获取token列表
 */
app.post('/api/token_list', function (req,res){
    if(!sign(req.body)){
        res.send(JSON.stringify({'code':'103','msg':'签名错误','data':[]}));
        return ;
    }
    let addr = req.body.addr;
    if(addr.length!==42){
        res.send(JSON.stringify({'code':'102','msg':'参数错误','data':[]}));
        return ;
    }
    bii(req.body);
    let addressObj = {};
    let returnObj = [];
    query("select * from addresses where address = ?",[addr],function (err,vals) {
        if(vals.length>0){
            addressObj = vals;
            query("select tokens.*,token_icons.icon_url token_ico_url from tokens left join token_icons on(tokens.token_addr=token_icons.token_addr) where address_id=? order by id",[vals[0].id],function (err,vals) {
                if(vals.length===0){
                    returnObj[0]={
                        "id": 100,
                        "address_id": addressObj[0].id,
                        "token_name": "ETH",
                        "token_num": 0,
                        "token_addr": '',
                        "token_symbol": "ETH",
                        "create_time": "2018-08-07T09:52:41.000Z",
                        "token_ico_url": 'http://ad.fshd.com/uploads/image/2018/08/10/default.png',
                        "decimals": 18,
                        "rate": "0"
                    };
                    return res.send(JSON.stringify({'code':'200','msg':'成功','data':returnObj}));
                    /* 8月23日注，对于新钱包，先给返回为数量0的ETH
                    http.get('http://api.ethplorer.io/getAddressInfo/'+addr+'?apiKey=freekey', function (re) {
                        let body = "";
                        re.on('data', function (chunk) {
                            body += chunk;
                        });
                        re.on('end', function (re) {
                            jsonObj = JSON.parse(body);
                            console.log(body)
                            if(!jsonObj.hasOwnProperty('error')){
                                console.log('eth 入库');
                                //eth入库
                                query('INSERT INTO `tokens` (`address_id`, `token_name`, `token_num`, `token_addr`, `token_symbol`, `decimals`, `rate`) VALUES (?,?,?,?,?,?,?)',[addressObj[0].id,'ETH',jsonObj['ETH']['balance']*1000000000000000000,conf.ETH_TOKEN,'ETH',18,0],function (err,vals){
                                    returnObj[0]={
                                        "id": Math.round(Math.random()*100000),
                                        "address_id": addressObj[0].id,
                                        "token_name": "ETH",
                                        "token_num": jsonObj['ETH']['balance']*1000000000000000000,
                                        "token_addr": conf.ETH_TOKEN,
                                        "token_symbol": "ETH",
                                        "create_time": "2018-08-07T09:52:41.000Z",
                                        "token_ico_url": 'http://ad.fshd.com/uploads/image/2018/08/10/default.png',
                                        "decimals": 18,
                                        "rate": "1.00000000000"
                                    };
                                    console.log(returnObj);
                                    if(jsonObj.hasOwnProperty('tokens')){//其它token入库
                                        console.log('token 入库');
                                        jsonObj['tokens'].forEach(function (item) {
                                            query('select * from tokens where address_id=? and token_addr=?',[addr,item['tokenInfo']['address']],function (err,vals) {
                                                if(vals.length===0){
                                                    //addressObj[0].id
                                                    query('INSERT INTO `tokens` (`address_id`, `token_name`, `token_num`, `token_addr`, `token_symbol`, `decimals`, `rate`) VALUES (?,?,?,?,?,?,?)',[addressObj[0].id,item['tokenInfo']['name'],0,item['tokenInfo']['address'],item['tokenInfo']['symbol'],item['tokenInfo']['decimals'],item['tokenInfo']['price']['rate']],function (err,vals) {
                                                        //查出来再返回
                                                        query('select * from tokens where address_id=? and token_addr=?',[addressObj[0].id,item['tokenInfo']['address']],function(err,vals){
                                                            returnObj.push(vals[0])
                                                        })
                                                    })
                                                }
                                            })
                                        })
                                    }
                                    return res.send(JSON.stringify({'code':'200','msg':'成功','data':returnObj}));
                                })

                            }else{
                                return res.send(JSON.stringify({'code':'110','msg':jsonObj['error']}));
                            }
                        })
                    })*/
                }else{
                    return res.send(JSON.stringify({'code':'200','msg':'成功','data':vals}));
                }
            })
        }else{
            res.send(JSON.stringify({'code':'101','msg':'该地址未初始化','data':[]}));

        }
    })
});



/**
 * 转账
 */
app.post('/api/transfer', function (req,res){
    let web3 = choosePeer();
    if(!sign(req.body)){
        res.send(JSON.stringify({'code':'103','msg':'签名错误','data':[]}));
        return ;
    }
    bii(req.body);
    let transStr = req.body.tx;
    let deviceId = req.body.device_id;
    console.log('transfer>>deviceId:'+deviceId+',transStr:'+transStr);
    logger.info('transfer>>deviceId:'+deviceId+',transStr:'+transStr);
    web3.eth.sendRawTransaction(transStr, function(err, hash) {
        console.log('------transh hash---:'+hash)
        if (!err) {
            var transaction = web3.eth.getTransaction(hash);
            console.log(transaction);
            logger.info('transfer>>deviceId:'+deviceId+', transfer success');
            return res.send({'code':'200','msg':'成功','data':transaction});
        } else {
            console.log(err);
            logger.error('transfer>>deviceId:'+deviceId+', transfer error.msg:'+err);
            return res.send({'code':'110','msg':err});
        }
    });
});

/**
 * 创建钱包时提交信息
 */
app.post('/api/wallet_gen', function (req,res){
    let address         = req.body.address;
    let deviceId        = req.body.device_id;
    let brand           = req.body.brand;
    let type            = req.body.ptype;
    let version         = req.body.sys_version;
    let appVersion      = req.body.app_version;

    if(!sign(req.body)){
        return res.send(JSON.stringify({'code':'103','msg':'签名错误','data':[]}));
    }
    if(!(address&&deviceId&&brand&&type&&version&&appVersion)){
        return res.send(JSON.stringify({'code':'102','msg':'参数错误'}));
    }
    bii(req.body);
    query("insert into addresses (`address`, `device_id`) values (?,?) on duplicate key update address = ?",[address,deviceId,address],function(err,vals){
        console.log(err);
    });
    return res.send(JSON.stringify({'code':'200','msg':'成功'}));
});

/**
 *  花费 gas 估算
 */
app.post('/api/gas_estimate', function (req, res){
    let web3 = choosePeer();
    if(!sign(req.body)){
        return res.send(JSON.stringify({'code':'103','msg':'签名错误','data':[]}));
    }
    let fromAddress     = req.body.from;
    let toAddress       = req.body.to;
    let symbol          = req.body.symbol.toUpperCase();
    let amount          = req.body.amount;
    let brand           = req.body.brand;
    let type            = req.body.ptype;
    let deviceId        = req.body.device_id;
    let version         = req.body.sys_version;
    let appVersion      = req.body.app_version;
    logger.info('gas_estimate>>fromAddress:'+fromAddress+',toAddress:'+toAddress+',symbol:'+symbol+',amount:'+amount+
        ',brand:'+brand+',type:'+type+',deviceId:'+deviceId+',version:'+version+',appVersion:'+appVersion);
    console.log('gas_estimate>>fromAddress:'+fromAddress+',toAddress:'+toAddress+',symbol:'+symbol+',amount:'+amount+
        ',brand:'+brand+',type:'+type+',deviceId:'+deviceId+',version:'+version+',appVersion:'+appVersion);
    if (!fromAddress || !toAddress || !symbol || 
        !amount || !brand || !type || !deviceId || !version || !appVersion ||
        fromAddress.length != 42 || toAddress.length != 42) {
        return res.send(JSON.stringify({'code':'403','msg':'params error!'}));
    }
    bii(req.body);
    let tokenAddress    = '';
    let estimateGas     = 0;
    let number      = web3.eth.getTransactionCount(fromAddress);
    console.log('------gas_estimate>>number::'+number);
    //let nonce       = web3.toHex(number);
    logger.debug('return json:'+JSON.stringify({'code':200, 'gas':200000, 'nonce': number}));
    return res.send(JSON.stringify({'code':200, 'gas':200000, 'nonce': number}));
    // query('select token_addr from tokens where token_symbol = ? limit 1', [symbol], function (err,vals){
    //     if (!err && vals.length > 0) {
    //         tokenAddress = vals[0].token_addr;
    //         logger.debug('get token addr from db, tokenAddress:'+tokenAddress);
    //         if (tokenAddress.length != 0) {
    //             estimateGas = gasPrice.estimateGas(amount, toAddress, tokenAddress, tokenerc20Contract, web3);
    //             logger.debug('estimateGas:'+estimateGas);
    //         }
    //     }
    //     let number      = web3.eth.getTransactionCount(fromAddress);
    //     //let nonce       = web3.toHex(number);
    //     logger.debug('return json:'+JSON.stringify({'code':200, 'gas':estimateGas, 'nonce': number}));
    //     return res.send(JSON.stringify({'code':200, 'gas':estimateGas, 'nonce': number}));
    // });
});

/**
 * 取gas price和gas limit
 */
app.post('/api/gas_price', function (req, res){
    if(!sign(req.body)){
        return res.send(JSON.stringify({'code':'103','msg':'签名错误','data':[]}));
    }
    let brand           = req.body.brand;
    let type            = req.body.ptype;
    let deviceId        = req.body.device_id;
    let version         = req.body.sys_version;
    let appVersion      = req.body.app_version;
    logger.debug('gas_price>>brand:'+brand+',type:'+type+',deviceId:'+deviceId+',version:'+version+',appversion:'+appVersion);
    if (!brand || !type || !deviceId || !version || !appVersion) {
        return res.send(JSON.stringify({'code':'403','msg':'params error!'}));
    }
    bii(req.body);
    let json = JSON.parse(JSON.parse(fs.readFileSync('./gas_price.json','utf-8')));
    json.gasLimit=conf.GAS_LIMIT;
    logger.info("gas_price>>deviceId:" + deviceId + " got gas price, price:" + JSON.stringify(json));
    return res.send(JSON.stringify(json));
});

/**
 * 客户端版本检测
 */
app.post('/api/update',function(req,res){
    if(!sign(req.body)){
        res.send(JSON.stringify({'code':'103','msg':'签名错误','data':[]}));
        return ;
    }
    let clientVersion = req.body.app_version;
    if(!clientVersion){
        res.send(JSON.stringify({'code':'101','msg':'参数错误'}));
    }
    bii(req.body);

    let json = fs.readFileSync('version_conf.json').toString();
    let jsonObj = JSON.parse(json);
    if(clientVersion<jsonObj['forceVersion']){
        jsonObj['forceUpdate'] = true;
    }else{
        jsonObj['forceUpdate'] = false;
    }

    if(clientVersion<jsonObj['lastestVersion']){
        jsonObj['suggestUpdate'] = true;
    }else{
        jsonObj['suggestUpdate'] = false;
    }
    res.send(JSON.stringify(jsonObj));
})
