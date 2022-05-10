import {Card} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import React from "react";


export const SessionCard = (props) => {

    const handleClick = () => {
        props.openSession(props.name)
    }

    return (

        <Card style={{width: '18rem'}}>
            <Card.Body>
                <Card.Title>{props.name}</Card.Title>
                <Button variant="primary" onClick={handleClick}>Open</Button>
            </Card.Body>
        </Card>
    )
}