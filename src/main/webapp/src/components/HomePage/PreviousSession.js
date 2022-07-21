import * as React from "react";
import {Fragment, useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {Button, Card} from "react-bootstrap";
import {request} from "../../utils/Request";

const uri = (id) => `/api/web/v1.0/non_auth/sessionStatus/${id}`
const url = '/api/web/v1.0/non_auth/is_in_session'
export const PreviousSession = () => {

    const [state, setState] = useState({ids: null, loading: true})
    console.log(state)

    useEffect(() => {
        const func_obj = {
            success: (data) => setState({ids: data.properties, loading: false}),
            failed: () => setState({ids: null, loading: false})
        }
        request(url, {method: 'GET'}, func_obj)
    }, [])

    if(state.ids != null) return <PreviousSessionCard ids={state.ids}/>
}

const InPreviousSessionCard = ({participantId, sessionId}) => {

    const [state, setState] = useState({content: null, loading: true})

    useEffect(() => {
        setState((prev) => { return {...prev, loading: true}})
        const s_func = (data) => {
            setState({
                content: <Fragment>
                    <Link className="btn btn-success" to={`/insession/${participantId}`}>Join session</Link>
                    <p><strong>Status:</strong> Visible for participants</p>
                </Fragment>,
                loading: false
            })
        }
        const f_func = () => {
            setState({
                content: <Fragment>
                    <Link className="btn btn-success" to={`/results/${participantId}/${sessionId}`}>Show results</Link>
                    <p><strong>Status:</strong> Not visible for participants</p>
                </Fragment>,
                loading: false
            })
        }
        const func_obj = {success: s_func, failed: f_func}
        return request(uri(participantId), {method: 'GET'}, func_obj).cancel
    }, [participantId])

    if(state.loading) return

    return(
        <Card>
            <Card.Title>Previous Session</Card.Title>
            <Card.Body>
                {state.content}
            </Card.Body>
        </Card>
    )
}

const MemorablePreviousSession = React.memo(InPreviousSessionCard)

const PreviousSessionCard = ({ids}) => {
    const {participantId,sessionId} = ids
    return(<MemorablePreviousSession participantId={participantId} sessionId={sessionId}/>)
}