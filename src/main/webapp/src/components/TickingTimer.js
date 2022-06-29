import * as React from "react";
import {useEffect, useState} from "react";
import {millisToTime} from "../utils/TimeUtils";

// start -> Start time in seconds
export const Timer = ({content, start}) => {

    const startDate = new Date(start * 1000).getTime()
    const [counter, setCounter] = useState(new Date().getTime() - startDate)

    useEffect(() => {
        const interval = setInterval(() => {
            setCounter(new Date().getTime() - startDate);
        }, 1000);

        return () => clearInterval(interval);
    }, [startDate]);

    return <p>{content}{millisToTime(counter)}</p>
}