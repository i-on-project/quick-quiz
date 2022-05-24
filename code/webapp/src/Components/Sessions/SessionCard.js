import {Card, Form} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import React, {useEffect, useState} from "react";
import {goPOST} from "../../Services/FetchService";


export const SessionCard = (props) => {

    const [status, setStatus] = useState(props.status)

    const handleOpenClick = () => {
        props.openSession(props.name, props.link)
    }
    const handleDeleteClick = () => {
        props.deleteSession(props.name, props.link)
    }


    const updateStatus = (newStatus) => {
        setStatus(newStatus)

        const setError = (error) => error !== null ? console.log(`Error Starting Session ${error}`) : null

        const setData = (data) => {
            console.log(`status Updated to ${newStatus}, response: ${data} -> Navigate somewhere`)
        }

        if (newStatus === 'STARTED')
            goPOST(props.startHref, '', setData, setError)
        else if (newStatus === 'CLOSED')
            goPOST(props.closeHref, '', setData, setError)
    }


    return (

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
                <Button variant="primary" onClick={handleDeleteClick} className="ms-3 mt-3">Delete</Button>
            </Card.Body>
        </Card>
    )
}