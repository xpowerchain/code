let Web3 = require('web3')
let fs = require('fs');
let conf  = require('../conf.js');
let func = function(){
    let peer = 0;
    let confItem = "";
    conf.RPC_HOSTS.forEach(function (item) {
        let provider_ = new Web3.providers.HttpProvider(item);
        let web3_ = new Web3(provider_);
        if(web3_.isConnected()){
            if(web3_.net.peerCount>peer){
                console.log(web3_.net.peerCount);
                confItem = item;
            }
        }
    });
    console.log('节点选择：'+confItem);
    if(confItem!==""){
        fs.writeFile('node_server.conf',confItem,function (err) {

        })
    }else{
        console.log("节点选择失败");
    }
}

module.exports = func;