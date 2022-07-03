import * as React from "react";
import {Fragment, useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {Button, Card} from "react-bootstrap";
import {request} from "../../utils/Request";

const uri = '/api/web/v1.0/non_auth/sessionStatus/'
const InPreviousSession = ({participant_id}) => {

    const [state, setState] = useState({content: null, loading: true})

    useEffect(() => {
        setState((prev) => { return {...prev, loading: true}})
        const s_func = (data) => {
            setState({
                content: <Fragment>
                    <Link className="btn btn-success" to={`/insession/${participant_id}`}>Join session</Link>
                    <p><strong>Status:</strong> Visible for participants</p>
                </Fragment>,
                loading: false
            })
        }
        const f_func = () => {
            setState({
                content: <Fragment>
                    <Button variant="success" disabled={true}>Join session</Button>
                    <p><strong>Status:</strong> Not visible for participants</p>
                </Fragment>,
                loading: false
            })
        }
        const func_obj = {success: s_func, failed: f_func}
        console.log(`${uri}/${participant_id}`)
        return request(`${uri}${participant_id}`, {method: 'GET'}, func_obj).cancel
    }, [participant_id])

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

const MemorablePreviousSession = React.memo(InPreviousSession)

export const PreviousSession = ({participant_id}) => {
    return(<MemorablePreviousSession participant_id={participant_id}/>)
}