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

let queryPromise = function( sql, values ) {
    return new Promise(( resolve, reject ) => {
        pool.getConnection(function(err, connection) {
            if (err) {
                reject( err )
            } else {
                connection.query(sql, values, ( err, rows) => {

                    if ( err ) {
                        reject( err )
                    } else {
                        resolve( rows )
                    }
                    connection.release()
                })
            }
        })
    })
}

module.exports=queryPromise