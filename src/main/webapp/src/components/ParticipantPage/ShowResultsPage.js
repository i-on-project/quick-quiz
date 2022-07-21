import * as React from "react";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {request} from "../../utils/Request";

const uri = (pid, sid) => `/api/web/v1.0/non_auth/history/${pid}/${sid}`
export const ShowResults = () => {

    const {participantId, sessionId} = useParams()

    const [state, setState] = useState({data: null, loading: true, problem: null})

    useEffect(() => {
        const func_obj = {
            success: (data) => setState(prev => { return {...prev, data: data.properties}})
        }
        request(uri(participantId, sessionId), {method: 'GET'}, func_obj)
    })
}