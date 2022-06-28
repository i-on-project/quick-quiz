export function secondsToString(unix_timestamp) {
    const date = new Date(unix_timestamp * 1000)
    const year = date.getFullYear()
    const month = "0" + date.getMonth()
    const day = "0" + date.getDay()
    const hours = "0" + date.getHours()
    const minutes = "0" + date.getMinutes()
    const seconds = "0" + date.getSeconds()

    return  year+'/'+month.substr(-2)+'/'+day.substr(-2)+' '+hours.substr(-2)+':'+minutes.substr(-2)+':'+seconds.substr(-2)
}