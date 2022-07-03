import * as React from "react"
import {Fragment, useCallback, useState} from "react";
import {Link, Navigate} from "react-router-dom"
import {Card} from "react-bootstrap";
import {parse_body, request_no_content} from "../../utils/Request";
import {Notification} from "../Notification";
import {ActionButton} from "../ActionButon";
import {getCurrentLocation} from "../../utils/LocationUtils";

export const SessionCard = (props) => {

    const {reload} = props
    const session = props.session.properties
    const {delete_href, start_href, close_href} = props.links
    const href = props.session.href

    const [problem, setProblem] = useState(null)
    const [redirect, setRedirect] = useState(null)

    const onCloseHandler = useCallback(() => setProblem(null), [])
    //const onClickOpenHandler = useCallback(() => setRedirect(`/session/${session.id}`), [session.id])

    const onClickDeleteHandler = useCallback(() => {
        const s_func = () => reload()
        const f_func = (problem) => setProblem(problem)
        const func_obj = {success: s_func, failed: f_func}
        return request_no_content(delete_href, {method: 'DELETE'}, func_obj).fetch
    }, [reload, delete_href])

    const onClickStartHandler = useCallback(async () => {
        const start_func = () => {
            const func_obj = {success: () => setRedirect(`/live_session/${session.id}`), failed: (problem) => setProblem(problem)}
            return request_no_content(start_href, {method: 'POST'}, func_obj).fetch
        }

        const failed_func = () => alert(`Unable to update the session geolocation to the current position`)

        if(session.geolocation === true) {
            let geolocation = null
            getCurrentLocation(location => {
                geolocation = `${location.coords.latitude},${location.coords.longitude}`
                const func_obj = {success: () => {}, failed: failed_func}
                request_no_content(href, {method: 'PUT', ...parse_body({geolocation: geolocation})}, func_obj)
            }, failed_func)
        }

        return start_func()
    }, [href, start_href, session.id, session.geolocation])

    /*TODO if session is using geolocation change to current location
    const onClickStartHandler = useCallback(() => {
        const s_func = () => setRedirect(`/live_session/${session.id}`)
        const f_func = (problem) => setProblem(problem)
        const func_obj = {success: s_func, failed: f_func}
        return request_no_content(start_href, {method: 'POST'}, func_obj).fetch
    }, [start_href, session.id])*/

    const onClickCloseHandler = useCallback(() => {
        const s_func = () => reload()
        const f_func = (problem) => setProblem(problem)
        const func_obj = {success: s_func, failed: f_func}
        return request_no_content(close_href, {method: 'POST'}, func_obj).fetch
    }, [reload, close_href])

    if(redirect != null) return <Navigate to={redirect}/>

    let action_button = null
    if(session.status === 'NOT_STARTED') action_button = <ActionButton style={{width: '16rem'}} variant="secondary" className="mt-3" perform={onClickStartHandler} content="Start"/>
    else if(session.status === 'STARTED') action_button = <ActionButton style={{width: '16rem'}} variant="danger" className="mt-3" perform={onClickCloseHandler} content="Close"/>

    return (
        <Fragment>
            <Notification problem={problem} onClose={onCloseHandler}/>
            <Card style={{width: '20rem'}}  className="mb-3">
                {
                    session.status === 'STARTED' ?
                        <Card.Title><Link to={`/live_session/${session.id}`}>{session.name}</Link></Card.Title> :
                        <Card.Title><Link to={`/session/${session.id}`}>{session.name}</Link></Card.Title>
                }
                <Card.Body>
                    <Card.Text>{session.description}</Card.Text>
                    <Card.Text>{session.status}</Card.Text>
                    {action_button}
                    {
                            session.status === 'STARTED' ? null :
                            <ActionButton style={{width: '16rem'}} variant="danger" className="mt-3" perform={onClickDeleteHandler} content="Delete"/>
                    }
                </Card.Body>
            </Card>
        </Fragment>
    )
}