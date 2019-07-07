var log4js              = require('log4js');

log4js.configure({
  appenders: { cheese: { type: 'file', filename: 'logs/imtoken.log' } },
  categories: { default: { appenders: ['cheese'], level: 'debug' } }
});

exports.log = function (appender) {
	return log4js.getLogger(appender);
}

module.exports = exports;