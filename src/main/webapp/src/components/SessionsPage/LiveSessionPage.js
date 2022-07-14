import * as React from "react";
import {Fragment, useCallback, useEffect, useState} from "react";
import {Button, Card, Col, Container, Modal, Row, Spinner} from "react-bootstrap";
import {Link, Navigate, useParams} from "react-router-dom";
import SockJsClient from 'react-stomp';
import {Notification} from "../Notification";
import {request} from "../../utils/Request";
import {getActionHref, getLinksFromEntity, getLinksHref} from "../../utils/SirenJson";
import {ProblemJson} from "../../utils/ProblemJson";
import {Timer} from "../TickingTimer";
import {NewQuizModal} from "../QuizzesPage/NewQuizModal";
import {SortQuizzesEntities} from "../../utils/models/QuizModel";
import {MutableQuizCard} from "../QuizzesPage/MutableQuizCard";

const href_unknown_problem = new ProblemJson("InvalidRequest", "The uri to fetch the data is unknown")
const uri = (id) => `/api/web/v1.0/auth/sessions/${id}`
const webSocketUri = '/insessionws'
const webSocketTopic = (id) => [`/queue/insession/${id}`]
export const LiveSession = () => {

    const {id} = useParams()

    const [session_state, setSessionState] = useState({data: null, loading: true, problem: null})
    const [quizzes_state, setQuizzesState] = useState({data: null, loading: true, problem: null})
    const [answers_state, setAnswersState] = useState({data: null, loading: true, problem: null})

    const [messages, setMessages] = useState({lastTimeFetch: null, newMessages: false})
    const [webSocketClient, setWebSocketClient] = useState(null)
    const [webSocketConnected, setWebSocketConnected] = useState(false)

    const [modal, setModal] = useState(false)

    const loadSession = useCallback(() => {
        setSessionState(prev => { return {...prev, loading: true}})

        const failed_func = (problem) => setSessionState((prev) => {
            prev.loading = false
            return {...prev, problem: problem}
        })
        const s_func_session = (data) => {
            setSessionState((prev) => {
                prev.data = data
                prev.loading = false
                return {...prev}
            })
        }
        const func_session = {success: s_func_session, failed: failed_func}
        return request(uri(id), {method: 'GET'}, func_session)
    }, [id])

    const loadQuizzes = useCallback(() => {
        setQuizzesState(prev => { return {...prev, loading: true}})

        const failed_func = (problem) => setQuizzesState((prev) => {
            prev.loading = false
            return {...prev, problem: problem}
        })
        if(session_state.data == null) failed_func(href_unknown_problem)
        const href = getLinksHref(session_state.data.links, 'related', 'Quizzes')
        if(href == null) failed_func(href_unknown_problem)

        const s_func_quizzes = (data) => setQuizzesState((prev) => {
            prev.data = data
            prev.loading = false
            return {...prev}
        })

        const func_quizzes = {success: s_func_quizzes, failed: failed_func}
        return request(href, {method: 'GET'}, func_quizzes)
    }, [session_state])

    const loadAnswers = useCallback(() => {
        setAnswersState(prev => { return {...prev, loading: true}})

        const failed_func = (problem) => setAnswersState((prev) => {
            prev.loading = false
            return {...prev, problem: problem}
        })
        if(session_state.data == null) failed_func(href_unknown_problem)
        const href = getLinksHref(session_state.data.links, 'related', 'Answers')
        if(href == null) failed_func(href_unknown_problem)

        const s_func_quizzes = (data) => setAnswersState((prev) => {
            prev.data = data
            prev.loading = false
            return {...prev}
        })

        const func_answers = {success: s_func_quizzes, failed: failed_func}
        return request(href, {method: 'GET'}, func_answers)
    }, [session_state])

    const onCloseSessionAlert = useCallback(() => setSessionState(prev => {return {...prev, problem: null}}), [])
    const onCloseQuizzesAlert = useCallback(() => setQuizzesState(prev => {return {...prev, problem: null}}), [])
    const onCloseAnswersAlert = useCallback(() => setAnswersState(prev => {return {...prev, problem: null}}), [])
    const onClickModalHandler = useCallback(() => setModal(true), [])
    const onCloseModalHandler = useCallback(() => setModal(false), [])
    const onConnectionHandler = useCallback(() => setWebSocketConnected(true), [])

    const refWebSocket = useCallback((client) => setWebSocketClient(client), [])
    const notifyQuizChange = useCallback(() => {
        if(webSocketClient == null) return
        if(webSocketConnected === false) return
        webSocketClient.sendMessage(`/topic/insession/${id}`, 'Q')
    }, [webSocketClient, webSocketConnected, id])

    const onMessageHandler = useCallback(() => {setMessages(prev => { return {...prev, newMessages: true}})}, [])

    useEffect(() => {
        return loadSession().cancel
    }, [loadSession])
    useEffect(() => {
        if(session_state.data == null) return
        return loadQuizzes().cancel
    }, [session_state.data, loadQuizzes])
    useEffect(() => {
        if(session_state.data == null) return
        if(quizzes_state.data == null) return
        setMessages({lastTimeFetch: Date.now(), newMessages: false})
        return loadAnswers().cancel
    }, [session_state, quizzes_state, loadAnswers])

    useEffect(() => {
        if(messages.lastTimeFetch == null) return
        if(messages.newMessages === true) {
            const elapsedTime = Date.now() - messages.lastTimeFetch
            if(elapsedTime >= 1000) {
                setMessages({lastTimeFetch: Date.now(), newMessages: false})
                loadAnswers()
            } else {
                return () => clearTimeout(setTimeout(() => {
                    setMessages({lastTimeFetch: Date.now(), newMessages: false})
                    loadAnswers()
                }, 1000 - elapsedTime))
            }
        }
    }, [messages, loadAnswers])

    const spinner = <div className="ms-3 text-center"><Spinner animation="border" style={{width: "3rem", height: "3rem"}}/></div>

    let modal_content = null
    if(modal) modal_content = <Modal show={modal}>
        <NewQuizModal reload={loadQuizzes} href={getActionHref(session_state.data.actions, "Add-Quiz")} onClose={onCloseModalHandler} notify={notifyQuizChange}/>
    </Modal>

    let main_content = null
    if(session_state.loading) main_content = spinner
    else {
        let session_content = null
        if(session_state.data != null) {
            const session = session_state.data.properties
            if(session.status !== 'STARTED') return <Navigate to={`/session/${session.id}`} />
            session_content = <Container>
                <Row>
                    <Card className={"mt-3"}>
                        <Col><Card.Title>Name: {session.name}</Card.Title>
                        <Card.Title>Description: {session.description}</Card.Title>
                        <Card.Title>Guest code: {session.guestCode}</Card.Title>
                        <Card.Title>Participants: {answers_state.data == null ? 'unknown' : answers_state.data.entities.length} / {session.limitOfParticipants}</Card.Title>
                        <Card.Title><Timer content={'Elapsed time: '} start={session.creationDate}/></Card.Title></Col>
                        <Col></Col>
                    </Card>
                </Row>
                <SockJsClient url={webSocketUri} topics={webSocketTopic(id)} onConnect={onConnectionHandler} onMessage={onMessageHandler} ref={refWebSocket}/>
            </Container>
        }
        let quizzes_content = null
        if(quizzes_state.loading) quizzes_content = spinner
        else if(quizzes_state.data != null && session_content != null) {
            const quizzes = quizzes_state.data.entities
            quizzes_content = <Container fluid="md">
                <hr/>
                {
                    webSocketConnected === false ? <Fragment>
                        <Row><h4>Connecting to participants...</h4><Spinner animation="border"/></Row>
                        <hr/>
                    </Fragment> : null
                }
                <Row><Button variant="success" className="mb-3" onClick={onClickModalHandler}>Add new quiz</Button></Row>
                <Row>{quizzes.sort(SortQuizzesEntities).map((elem, idx) => {
                    return <MutableQuizCard
                        key={elem.properties.id} quiz={elem.properties} reload={loadQuizzes} notify={notifyQuizChange}
                        answers={answers_state.data == null ? null : answers_state.data.entities}
                        href={{self: elem.href, status: getLinksFromEntity(quizzes[idx], 'update_status')}}/>
                })}</Row>
                {modal_content}
            </Container>
        }
        if(session_content != null) main_content = <Fragment>
            {session_content}
            <Notification problem={quizzes_state.problem} onClose={onCloseQuizzesAlert}/>
            <Notification problem={answers_state.problem} onClose={onCloseAnswersAlert}/>
            {quizzes_content}
        </Fragment>
    }
    if(main_content == null) main_content = <Fragment>
        <h1>404 - Session not found</h1>
        <Link to="/sessions">Go back</Link>
    </Fragment>

    return(
        <Fragment>
            <Notification problem={session_state.problem} onClose={onCloseSessionAlert}/>
            {main_content}
        </Fragment>
    )
}