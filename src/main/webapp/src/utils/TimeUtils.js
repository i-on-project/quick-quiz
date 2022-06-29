export function secondsToString(unix_timestamp) {
    const date = new Date(unix_timestamp * 1000)
    const year = date.getFullYear()
    const month = "0" + date.getMonth()
    const day = "0" + date.getDay()
    return year+'/'+month.substr(-2)+'/'+day.substr(-2)+' '+dateToTime(date)
}

export function dateToTime(date) {
    const hours = "0" + date.getHours()
    const minutes = "0" + date.getMinutes()
    const seconds = "0" + date.getSeconds()
    return hours.substr(-2)+':'+minutes.substr(-2)+':'+seconds.substr(-2)
}

export function millisToTime(millis) {
    const hours = '0' + Math.floor(millis / 1000 / 60 / 60)
    const minutes = '0' + Math.floor((millis / 1000 / 60) - (hours * 60))
    const seconds = '0' + Math.floor((millis / 1000) - (minutes * 60))
    return hours.substr(-2)+':'+minutes.substr(-2)+':'+seconds.substr(-2)
}