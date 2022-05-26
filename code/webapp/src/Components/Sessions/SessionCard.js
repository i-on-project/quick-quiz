import {Card, Form} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import React, {Fragment, useState} from "react";
import {goPOST} from "../../Services/FetchService";
import {Navigate} from "react-router-dom";

export const SessionCard = (props) => {

    const [status, setStatus] = useState(props.status)
    const [inSession, setInSession] = useState(false)

    const handleOpenClick = () => {
        if (status !== 'STARTED')
            props.openSession(props.name, props.link)
        else
            setInSession(true)

    }
    const handleDeleteClick = () => {
        if (status !== 'STARTED')
            props.deleteSession(props.name, props.link)

    }


    const updateStatus = (newStatus) => {
        setStatus(newStatus)

        const setError = (error) => {

            console.log(error)
            if(error.type === 'LiveSessionAlreadyExists') {
                alert('Another Session is already Live. Please close that session before starting a new one.')
                setStatus('NOT_STARTED')
            } else {
                alert('An error occurred. PLease try again and if the error persists contact your administrator.')
            }

        }

        const setData = (data) => {
            console.log(`status Updated to ${newStatus}, response: ${data} -> Navigate somewhere`)
            if (newStatus === 'STARTED') {
                setInSession(true)
            } else {
                props.reloadSessions()
            }

        }

        if (newStatus === 'STARTED')
            goPOST(props.startHref, '', setData, setError)
        else if (newStatus === 'CLOSED')
            goPOST(props.closeHref, '', setData, setError)
    }


    return (
        <Fragment>
            {inSession && (<Navigate to={`/owninsession/${props.id}`}/>)}
            <Card style={{width: '18rem'}} className="me-3 mb-3">
                <Card.Body>
                    <Card.Title>{props.name}</Card.Title>
                    <Form.Select value={status}
                                 onChange={(e) => updateStatus(e.target.value)}>
                        {status === 'NOT_STARTED' && <option value='NOT_STARTED'>Not Started</option>}
                        {status !== 'CLOSED' && <option value='STARTED'>Started</option>}
                        {status !== 'NOT_STARTED' && <option value='CLOSED'>Closed</option>}
                    </Form.Select>
                    <Button variant="primary" onClick={handleOpenClick} className="mt-3">Open</Button>
                    {status !== 'STARTED' &&
                        <Button variant="primary" onClick={handleDeleteClick} className="ms-3 mt-3">Delete</Button>}
                </Card.Body>
            </Card>
        </Fragment>
    )
}