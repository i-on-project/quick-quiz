import React, {Fragment, useEffect, useState} from "react";
import {Container, Modal, Row, Spinner} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import {SessionCard} from "./SessionCard";
import {Navigate} from "react-router-dom";
import {goDEL, goGET, goPOST} from "../../Services/FetchService";
import {CreateSessionModal} from "./CreateSessionModal";
import {getActionHref, getEntityLinksHref, getLinksFromEntity} from "../../Services/SirenService";


export const Sessions = () => {

    const [sessions, setSessions] = useState(null)
    const [sessionId, setSessionId] = useState(null)
    const [loading, setLoading] = useState(false)
    const [inSession, setInSession] = useState(false)
    const [newSession, setNewSession] = useState(false)
    const [page, setPage] = useState(0)
    const [totalPages, setTotalPages] = useState(-1)
    const [sessionCreated, setSessionCreated] = useState(null)


    const getSessions = () => {

        const setError = (error) => {
            alert(`Failed to update Session from ${`/api/web/v1.0/auth/sessions?page=${page === -1 ? 0 : page}`} with error ${error}`)
        }

        const setData = (data) => {
            setSessions(data)
            const nPages = Math.floor(data.properties.total / 10)
            setTotalPages(nPages === 0 ? 1 : nPages)
        }

        goGET(`/api/web/v1.0/auth/sessions?page=${page === -1 ? 0 : page}`, setData, setError, setLoading)
    }

    useEffect(() => {

        setLoading(true)
        getSessions()

    }, [page])


    const createSession = (newSession) => {
        setLoading(true)
        const setData = (data) => {
            handleCLose()
            console.log(data)
            setSessionCreated(data.properties)
            setSessionId(data.properties.id)
        }

        const setError = (error) => {
            if (error !== null)
                alert('Session was not created and Error received: ' + error)
        }

        const apiLink = getActionHref(sessions.actions, "Create-Session")

        if(apiLink !== null)
            goPOST(apiLink, newSession, setData, setError, 'POST', setLoading)
        else {
            setError("Missing Action!")
            setLoading(true)
        }

    }

    //const goToSession = (id) => <Redirect to={`\/${id}`} />
/*    const openSession = (name, link) => {
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

    const deleteSession = (id) => {
        setLoading(true)
        const setData = (data) => {
            console.log(data)
            getSessions()

        }

        const setError = (error) => {
            if (error !== null)
                alert('Session was not Deleted and Error received: ' + error)
        }

        console.log(sessions.entities)
        const apiLink = getEntityLinksHref(sessions.entities, id, "delete")

        if (apiLink !== null)
            goDEL(apiLink, setData, setError, setLoading)

    }

    const handleCLose = () => setNewSession(false)


    const newButton = () => (
        <Button className="btn btn-success" type="submit" className="mb-3"
                onClick={() => setNewSession(true)}> New Session
        </Button>
    )

    const handleNext = () => {
        if (page >= totalPages) setPage(0)
        else setPage((curr) => curr + 1)
    }

    const handlePrev = () => {
        if (page <= 0) setPage(totalPages)
        else setPage((curr) => curr - 1)
    }

    return (
        <Fragment>
            {sessionId !== null && <Navigate to={`./${sessionId}`} session={sessionCreated}/>}
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
                                     startHref = {getLinksFromEntity(e, "start")}
                                     closeHref = {getLinksFromEntity(e, "close")}
                                     openSession={() => openSession(e.properties.id)}
                                     deleteSession={() => deleteSession(e.properties.id)}
                                     reloadSessions={() => getSessions()}
                        />)
                    }
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

