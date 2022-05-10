import React, {useEffect, useState} from "react";
import {Card, Container, Row} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import {createSession, getAllSessions} from "../../Services/SessionService";
import {Session} from "../../Data/SessionModel";
import {SessionCard} from "./SessionCard";


export const Sessions = () => {

    const [sessions, setSessions] = useState(null)
    const [error, setError] = useState(null)
    const [loading, setLoading] = useState(false)


    useEffect(() => {
        setLoading(true)
        getAllSessions(setSessions, setError)
    }, [])

    useEffect(() => {
        if (sessions || error)
            setLoading(false)
    }, [sessions, error])

    const newSession = () => {

        const setData = (data) => {
            alert('Session was created and Data received: ' + data);
        }

        const setError = (error) => {
            alert('Session was not created and Error received: ' + error);
        }
        const current = new Date()
        const submitData = new Session("newSession", "A TestSession" + current.toLocaleTimeString(), 99)

        createSession(submitData, setData, setError)
    }

    const openSession = (session) => {
        console.log(session)
    }


    return (
        <div>
            <Container>
                <Row>
                    <h1>Sessions</h1>
                </Row>
            </Container>
            <Container>
                <Row>

                    <Button className="btn btn-success" type="submit"
                            onClick={newSession}> New Session
                    </Button>

                    {loading === false && sessions && sessions.entities.map(e => <SessionCard key={e.properties.name}
                                                                                              name={e.properties.name}
                                                                                              openSession={openSession}/>)}
                </Row>
            </Container>
        </div>
    )
}

