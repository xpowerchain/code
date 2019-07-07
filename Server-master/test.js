var mqtt = require('mqtt')
let moment = require('moment');
var Web3                = require('web3')
var fs                  = require('fs')
var ethereumjsWallet    = require('ethereumjs-wallet'); //引入以太坊nodejs操作钱包支持
var Tx                  = require("ethereumjs-tx"); //引入以太坊js交易支持
let dUtil               = require("./libs/date_util");
let math = require('mathjs');
// let bigNum              = require("bignum")

// getBigNumber(0.123,1000000000000000000).then(result => {
//     console.log('-------'+JSON.stringify(result))
// });

// console.log('-------'+JSON.stringify(a))
//
// console.log('0000000000000000');
// let b = bigNum(7777777)
// console.log(b.mul(1000000000000000000));


//
// async function getBigNumber (value, mulVal) {
//     console.log('11111111111111111111');
//     let bignum = await bigNum(value);
//     console.log('22222222222222');
//
//     return await bignum.mul(mulVal)
// }
//
// function get() {
//     return new Promise((resolve, reject)=>{
//
//     });
// }

/*let options = {
    host: '39.107.158.137',
    port:1883,
    username:'tokenServer',
    password:'tks_SucVER290',
    protocol:'tcp'
}*/


/*let options = {
    host: '39.107.158.137',
    port:1883,
    username:'tokenCli',
    password:'cli280*UtyRNa',
    protocol:'tcp'
}

var client  = mqtt.connect(options)

client.on('connect', function () {
    client.subscribe('wallet/transfer/0xd6af0ff6a31e163e8a9c045805aaca25417b1efc', function (err) {
        if (!err) {
            client.publish('wallet/transfer/0xd6af0ff6a31e163e8a9c045805aaca25417b1efc', 'Hello mqtt')

            client.publish('wallet/transfer/0xd6af0ff6a31e163e8a9c045805aaca25417b1efc', 'tt')
        }
    })
})

client.on('message', function (topic, message) {
    // message is Buffer
    console.log(message.toString())
    //client.end()
})

client.on('error',function (err) {
    console.log(err)
})*/


//console.log(math.format(9.99999999988e+28,{exponential:{lower:0,upper:Infinity}}))
//console.log(math.format(Number('9.99999999988e+28'),{"lowerExp":0,"upperExp":Infinity}))
/*
let tet = moment(1532364829*1000).format('YYYY-MM-DDTHH:mm:ss.000')+'Z';
console.log(tet);
*/
/*
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

balance = web3.eth.getBalance(item.address).toString();
console.log(balance);
*/

//console.log(0.0023*1000000000000000000)
let web3 = new Web3(new Web3.providers.HttpProvider('http://52.192.51.25:8545'));
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
//
// // 代币合约地址
// var tokenAddress = '0x2259133b1aa6b7f0b101559bf76091f383141b69';
// // 部署合约实例
// var tokenerc20Contract = web3.eth.contract(abi).at(tokenAddress);
//
// var myEvent = tokenerc20Contract.Transfer({some: 'args'}, {fromBlock: 0, toBlock: 'latest'});
// myEvent.watch(function(error, result){
//     console.log("代币tx列表::" + JSON.stringify(result));
//
// });
console.log(22222);
console.log(math.format(0,{"lowerExp":0,"upperExp":Infinity}));
console.log(23333333);
console.log((9.1e-7).toFixed(18).replace(/\.?0+$/, ""));
console.log(0.019216681125*1000000000000000000);