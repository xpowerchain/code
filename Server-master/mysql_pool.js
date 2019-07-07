// get the client
let mysql = require('mysql2');

// Create the connection pool. The pool-specific settings are the defaults
// let pool = mysql.createPool({
//     host: '47.93.160.183',
//     user: 'fshd',
//     password: 'fshd2017',
//     database: 'token_server',
//     waitForConnections: true,
//     connectionLimit: 10,
//     queueLimit: 0,
//     charset:'UTF8_GENERAL_CI'
// });

let pool = mysql.createPool({
    host: '127.0.0.1',
    user: 'tokenServer',
    password: 'hwalk1Na__Q',
    database: 'token_server',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0,
    charset:'UTF8_GENERAL_CI'
});

let mysql_pool=function(sql,param,callback){
    pool.getConnection(function(err,conn){
        if(err){
            callback(err,null,null);
        }else{
            conn.query(sql,param,function(qerr,vals,fields){
                //释放连接
                conn.release();
                //事件驱动回调
                callback(qerr,vals,fields);
            });
        }
    });
};


module.exports=mysql_pool;
