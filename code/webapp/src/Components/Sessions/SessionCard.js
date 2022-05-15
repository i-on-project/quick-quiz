import {Card, Form} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import React, {useEffect, useState} from "react";
import {goPOST} from "../../Services/FetchService";


export const SessionCard = (props) => {

    const [status, setStatus] = useState(props.status)

    const handleClick = () => {
        props.openSession(props.name, props.link)
    }

    const updateStatus = (newStatus) => {
        setStatus(newStatus)

        const setError = (error) => error !== null ? console.log(`Error Starting Session ${error}`) : null

        const setData = (data) => {
            if(data !== null)
                console.log(`status Updated to ${newStatus}, response: ${data} -> Navigate somewhere`)
        }

        console.log(`Status ${newStatus}`)
        if(newStatus === 'STARTED')
            goPOST(`/api/web/v1.0/auth/sessions/${props.id}/live`,'', setData, setError)
        else if(newStatus === 'CLOSED')
            goPOST(`/api/web/v1.0/auth/sessions/${props.id}/close`,'', setData, setError)
    }


    return (

        <Card style={{width: '18rem'}}>
            <Card.Body>
                <Card.Title>{props.name}</Card.Title>
                <Button variant="primary" onClick={handleClick}>Open</Button>
                <Form.Select value={status}
                             onChange={(e) => updateStatus(e.target.value)}>
                    {status === 'NOT_STARTED' && <option value='NOT_STARTED'>Not Started</option>}
                    {status !== 'CLOSED' && <option value='STARTED'>Started</option> }
                    {status !== 'NOT_STARTED' && <option value='CLOSED'>Closed</option> }
                </Form.Select>
            </Card.Body>
        </Card>
    )
}