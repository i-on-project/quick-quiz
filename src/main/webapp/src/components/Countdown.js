import {useEffect, useState} from "react";
import {millisToTime} from "../utils/TimeUtils";

export const Countdown = ({deadline}) => {
    const endDate = new Date(deadline * 1000).getTime()
    const [counter, setCounter] = useState(endDate - new Date().getTime())

    useEffect(() => {
        const interval = setInterval(() => {
            setCounter(_ => {
                const value = endDate - new Date().getTime()
                if(value <= 0) { clearInterval(interval); return 0 }
                else return value
            });
        }, 1000);

        return () => clearInterval(interval);
    }, [endDate]);

    return millisToTime(counter)
}