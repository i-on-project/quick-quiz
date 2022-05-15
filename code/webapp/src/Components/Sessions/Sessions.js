import React, {Fragment, useEffect, useState} from "react";
import {Card, Container, Modal, Row, Spinner} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import {createSession, getAllSessions, getSession} from "../../Services/SessionService";
import {SessionCard} from "./SessionCard";
import {Navigate} from "react-router-dom";
import {goGET, goPOST} from "../../Services/FetchService";
import {CreateSessionModal} from "./CreateSessionModal";
import {useLocation, useParams} from "react-router";


export const Sessions = () => {

    const [sessions, setSessions] = useState(null)
    const [sessionId, setSessionId] = useState(null)
    const [error, setError] = useState(null)
    const [loading, setLoading] = useState(false)
    const [inSession, setInSession] = useState(false)
    const [newSession, setNewSession] = useState(false)
    const [page, setPage] = useState(0)
    const [totalPages, setTotalPages] = useState(-1)


    useEffect(() => {

        setLoading(true)

        goGET(`/api/web/v1.0/auth/sessions?page=${page === -1 ? 0 : page}`, setSessions, setError)
        //getAllSessions(setSessions, setError)
    }, [page])

    useEffect(() => {

        if (sessions || error) {
            setLoading(false)
            const npages = Math.floor(sessions.properties.total / 10)
            setTotalPages( npages === 0 ? 1 : npages)
        }

    }, [sessions, error])

    const createSession = (newSession) => {

        const setData = (data) => {
            alert('Session was created and Data received: ' + data)
        }

        const setError = (error) => {
            if (error !== null)
                alert('Session was not created and Error received: ' + error)
        }

        const apiLink = sessions.actions.find(a => a.name === 'Create-Session').href
        goPOST(apiLink, newSession, setData, setError)

    }

    /*const openSession = (name, link) => {
        setLoading(true)
        //console.log(`name: ${name} -> link: ${link}`)
        const setError = (error) => {
            if (error !== null)
                alert(`Failed to retrieve Session from ${link} with error ${error}`)
        }
        getSession(link, setSession, setError)

        setInSession(true)
        setLoading(false)
    }*/

    const openSession = (id) => {
        setInSession(true)
        setSessionId(id)
    }

    const goBack = () => {
        setInSession(false)
    }

    const handleCLose = () => setNewSession(false)

    /*    const backButton = () => (
            <Button className="btn btn-success" type="submit"
                    onClick={goBack}> Back
            </Button>
        )*/

    const newButton = () => (
        <Button className="btn btn-success" type="submit"
                onClick={() => setNewSession(true)}> New Session
        </Button>
    )

    const handleNext = () => {
        console.log(`Number of pages: ${totalPages}`)
        if (page >= totalPages) setPage(0)
        else setPage((curr) => curr + 1)
    }

    const handlePrev = () => {
        if (page <= 0) setPage(totalPages)
        else setPage((curr) => curr - 1)
    }

    return (
        <Fragment>
            <Container>
                <Row>
                    <h1>Sessions</h1>
                </Row>
                {loading === false && inSession === false && (newButton())}
                {/*{loading === false && inSession === true && session !== null && (backButton())}*/}

            </Container>
            <Container>
                <Row>
                    {loading === true && (<Spinner animation="grow"/>)}
                    {loading === false && inSession === false && sessions !== null && sessions.entities.map(e =>
                        <SessionCard key={e.properties.name} /*TODO: use some other thing*/
                                     name={e.properties.name}
                                     link={e.href}
                                     id={e.properties.id}
                                     status={e.properties.status}
                                     openSession={() => openSession(e.properties.id)}/>)}
                    {loading === false && inSession === true && inSession === true && (
                        <Navigate to={`/sessions/${sessionId}`}/>)}
                    {/*  {loading === false && inSession === true && session !== null && (
                        <Session session={session} quizLink={session.links[0]}/>)}*/}
                    {loading === false && (
                        <Modal show={newSession}>
                            <CreateSessionModal createSession={createSession} handleClose={handleCLose}/>)
                        </Modal>)}
                </Row>

                <Row>
                    <Container>
                        <Button variant="primary" onClick={handlePrev}>Prev</Button>
                        <Button variant="primary" onClick={handleNext}>Next</Button>
                    </Container>
                </Row>
            </Container>
        </Fragment>
    )
}

