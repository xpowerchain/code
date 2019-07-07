var crypto = require('crypto');
var qs = require('querystring');

function signCheck(param) {
    if(!param){
        return false;
    }
    let token = '0c4d0f8bf7427b42058acee64cbfe3a7';
    let keys = [];
    let values = [];
    let sortedObj = {};
    sign = param.sign;
    delete param.sign;
    for(let a in param){
        keys.push(a);
        values.push(param[a]);
    }
    keys.sort();
    keys.forEach(function (value) {
        sortedObj[value] = param[value];
    })
    sortedObj['token'] = token;
    let sortedStr = qs.stringify(sortedObj);
    console.log(sortedStr);
    let md5 = crypto.createHash('md5');
    let result = md5.update(sortedStr).digest('hex');
    console.log(result);
    if(result === sign){
        return true;
    }else{
        return false;
    }
}
module.exports = signCheck;