
// 返回gas价格 wei单位
app.get('/', function (req, res) {
  var gasPrice = web3.eth.gasPrice;
  res.send('gasPrice:'+gasPrice);
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

// 另外一种启动httpserver的方法
var httpServer = 
var server = app.listen(8081, function () {
    var host = server.address().address
    var port = server.address().port
    console.log("访问地址为 http://%s:%s", host, port)
})

// 使用交易hash从链上获取交易详情
console.log("=====txInfo::"+JSON.stringify(web3.eth.getTransaction("0x76ffbaa4fdc38795eb830b451ddddaaf6b6b22a0afd966ce473f5a0bc05b9c4e")));
// 使用blocknum从链上获取交易详情
console.log("=====blockInfo::"+JSON.stringify(web3.eth.getBlock("0x06a779d4dd746d0f1c96018ddcd84c241f39fbea1dfb879703358386c5ff7585")));
