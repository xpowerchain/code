
let dUtil = function () {
    let date = new Date();
    let dateStr = date.getFullYear()+':'+date.getMonth()+':'+date.getDate()+' '+date.getHours()+':'+date.getMinutes()+':'+date.getSeconds()+':'+date.getMilliseconds();
    return dateStr;
}

module.exports = dUtil;