import * as React from "react";
import {useEffect, useState} from "react";
import {millisToTime} from "../utils/TimeUtils";

// start -> Start time in seconds
export const Timer = ({content, time}) => {

    const start = time == null ? -1 : time
    const startDate = start * 1000
    const [counter, setCounter] = useState(new Date().getTime() - startDate)

    useEffect(() => {
        if(startDate < 0) return

        const interval = setInterval(() => {
            setCounter(new Date().getTime() - startDate);
        }, 1000);

        return () => clearInterval(interval);
    }, [startDate]);

    if(startDate < 0) return <p>{content}Unknown error</p>
    return <p>{content}{millisToTime(counter)}</p>
}