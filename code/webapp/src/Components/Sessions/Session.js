import React, {useEffect, useState, Fragment, useReducer} from "react";
import {Card, Container, Form, FormControl, FormLabel, InputGroup, Modal, Row} from "react-bootstrap";
import {EditableInput} from "../UtilComponents/EditableInput";
import Button from "react-bootstrap/Button";
import {goGET, goPOST} from "../../Services/FetchService";
import {SessionCard} from "./SessionCard";
import {QuizCard} from "../Quizzes/QuizCard";
import {CreateEditQuizModal} from "../Quizzes/CreateEditQuizModal";
import {getSession} from "../../Services/SessionService";
import {useParams} from "react-router";
import {SessionForm} from "./SessionForm";
import {getActionHref, getEntityLinksHref, getLinksHref} from "../../Services/SirenService";

export const Session = (props) => {

    const initialState = {session: null}
    const [loading, setLoading] = useState(false)
    const [session, setSession] = useState(null)
    const [quizzes, setQuizzes] = useState(null)
    const [quizzesLinks, setQuizzesLinks] = useState(null)
    const [show, setShow] = useState(false)
    const [error, setError] = useState(null)
    const [title, setTitle] = useState('')
    const {id} = useParams()


    const reducer = (state, action) => {
        switch (action.type) {
            case 'loadQuizzes':
                setSession(action.payload)
                return loadQuizzes();
            default:
                return;
        }
    }
    const loadQuizzes = () => {
        if (session.properties.quizzes.length > 0) {
            //console.log(session.links[0].href)
            const setError = (error) => {
                if (error !== null)
                    alert(`Failed to retrieve Session from ${session.links[0].href} with error ${error}`)
            }

            const compareOrder = (a, b) => {
                if (a < b) return -1
                if (a > b) return 1
                return 0
            }

            const setData = (data) => {
                if (data) {
                    const t = data.properties.sort((a, b) => compareOrder(a.order, b.order))
                    setQuizzes(t)
                }
            }
            goGET(session.links[0].href, setData, setError)
        }
    }

    const [state, dispatch] = useReducer(reducer, initialState);

    useEffect(() => {
        /*Prevent reset state*/
        const setSessionError = (error) => {
            if (error !== null) {
                console.log(`Failed to get session with ID: ${id} with error ${error}`)
                setError(error)
            }
        }
        const getMeSession = (data) => {
            dispatch({type: 'loadQuizzes', payload: data})
        }

        goGET(`/api/web/v1.0/auth/sessions/${id}`, getMeSession, setSessionError)

    }, [id])



    useEffect(() => {
        if (session !== null) {
            setTitle(session.properties.name)
            if (session.properties.quizzes.length > 0) {
                getQuizzes()
            }
        }

    }, [session])

    const getQuizzes = () => {
        const setError = (error) => {
            if (error !== null)
                alert(`Failed to retrieve Session from ${getLinksHref(session.links, "related")} with error ${error}`)
        }

        const compareOrder = (a, b) => {
            if (a < b) return -1
            if (a > b) return 1
            return 0
        }

        const setData = (data) => {
            if (data) {
                const quizList = data.entities.map(e => e)
                const t = quizList.sort((a, b) => compareOrder(a.properties.order, b.properties.order))
                setQuizzes(t)
            }
        }
        goGET(getLinksHref(session.links, "related"), setData, setError)
    }

    const openSession = (name, link) => {
        setLoading(true)
        const setError = (error) => {
            if (error !== null)
                alert(`Failed to retrieve Session from ${link} with error ${error}`)
        }
        getSession(link, setSession, setError)

        setLoading(false)
    }

    const newQuiz = () => {
        setShow(true)
    }
    const handleClose = (reload = false) => {
        setShow(false)
        //if (reload)

    }


    const newButton = () => (
        <Button className="btn btn-success left" type="submit"
                onClick={newQuiz}> Add Quiz
        </Button>
    )

    const createQuizHandler = (quiz) => {
        const setError = (error) => error !== null ? console.log(`Error Creating with error ${error} `) : null
        const setData = (data) => {
            console.log(`Created Quiz Successfully!! Response: ${data}`)
            handleClose()
            getQuizzes()
        }
        const apiLink = session.actions.find(a => a.name === 'Add-Quiz').href
        goPOST(apiLink, quiz, setData, setError)

    }

    const updateSessionHandler = (updatedSession) => {
        setLoading(true)
        const setError = (error) => {
            if (error !== null)
                alert(`Failed to update Session from ${updatedSession} with error ${error}`)
        }

        const apiLink = getActionHref(session.actions, 'Update-Session')
        //const method = session.actions.find(a => a.name === 'Update-Session').method //TODO: getMethod

        goPOST(apiLink, updatedSession, null, setError, 'PUT', setLoading)
    }

    return (
        <Fragment>
            <Container>
                <Row>
                    <Card>
                        <Card.Body>

                            <Card.Title>{title}</Card.Title>
                            {session !== null && (
                                <SessionForm session={session.properties} updateSession={updateSessionHandler}/>)}
                        </Card.Body>
                    </Card>
                </Row>
            </Container>
            {/******************** if there are quizzes *********************************/}
            <Container>
                <Row>
                    {quizzes !== null && quizzes.length > 0 && (
                        quizzes.map(q => <QuizCard key={q.properties.id}
                                                   name={q.properties.question}
                                                   data={q.properties}
                                                   quizHref={q.href}
                                                   reloadQuizzes={getQuizzes}
                        />)
                    )}
                </Row>
            </Container>
            <Container>
                <Row>
                    {newButton()}
                </Row>
            </Container>
            <Modal show={show}>
                <CreateEditQuizModal handleClose={handleClose} createQuiz={createQuizHandler}/>
            </Modal>
        </Fragment>
    )

}