let query = require('../mysql_pool.js');

let bii = function (params) {
    console.log('bii debug'+JSON.stringify(params));
    if(params['device_id']&&params['brand']&&params['ptype']&&params['sys_version']&&params['app_version']){
        query('select * from device_info where did=?',params['device_id'],function (err,vals) {
            if(vals.length===0){
                query('INSERT INTO `device_info` (`did`, `brand`, `ptype`, `sys_version`,`app_version`) VALUES (?,?,?,?,?)',[params['device_id'],params['brand'],params['ptype'],params['sys_version'],params['app_version']],function (err,vals) {
                    console.log(err);
                })
            }else{
                query('UPDATE `device_info` SET `brand`=?, `ptype`=?, `sys_version`=?, `app_version`=? WHERE `id`=?',[params['brand'],params['ptype'],params['sys_version'],params['app_version'],vals[0]['id']],function (err,vals) {

                })
            }
        })
    }
}

module.exports = bii;