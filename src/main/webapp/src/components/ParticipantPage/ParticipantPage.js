import * as React from "react";
import {Fragment, useCallback, useEffect, useState} from "react";
import {Link, useParams} from "react-router-dom";
import {Container, Row, Spinner} from "react-bootstrap";
import SockJsClient from 'react-stomp';
import {request} from "../../utils/Request";
import {Notification} from "../Notification";
import {AnswerQuizCard} from "./AnswerQuizCard";

const webSocketUri = '/insessionws'
const webSocketTopic = (sessionId) => [`/topic/insession/${sessionId}`]
const answer_uri = (id) => `/api/web/v1.0/non_auth/answer/${id}`
const quizzes_uri = (id) => `/api/web/v1.0/non_auth/quiz/session/${id}`
export const ParticipantPage = () => {

    const {id} = useParams()
    const [quizzes, setQuizzes] = useState({data: null, loading: true, problem: null})
    const [answers, setAnswers] = useState({data: null, loading: true, problem: null})
    const [webSocketClient, setWebSocketClient] = useState(null)
    const [webSocketConnected, setWebSocketConnected] = useState(false)

    const loadQuizzes = useCallback(() => {
        const failed_func = (problem) => setQuizzes(prev => {return {...prev, loading: false, problem: problem}})
        const s_func_session = (data) => setQuizzes(prev => {return {...prev, data: data, loading: false}})
        const func_obj = {success: s_func_session, failed: failed_func}
        return request(quizzes_uri(id), {method: 'GET'}, func_obj)
    }, [id])

    const loadAnswers = useCallback(() => {
        setAnswers(prev => { return {...prev, loading: true}})
        const failed_func = (problem) => setAnswers(prev => {return {...prev, loading: false, problem: problem}})
        const s_func_session = (data) => setAnswers(prev => {return {...prev, data: data, loading: false}})
        const func_obj = {success: s_func_session, failed: failed_func}
        return request(answer_uri(id), {method: 'GET'}, func_obj)
    }, [id])

    const onCloseQuizzesHandler = useCallback(() => setQuizzes(prev => {return {...prev, problem: null}}), [])
    const onConnectionHandler = useCallback(() => setWebSocketConnected(true), [])

    const refWebSocket = useCallback((client) => setWebSocketClient(client), [])
    const notifyAnswerChange = useCallback(() => {
        if(webSocketClient == null) return
        if(answers.data == null) return
        webSocketClient.sendMessage(`/queue/insession/${answers.data.sessionId}`, 'A')
    }, [webSocketClient, answers])

    useEffect(() => {
        return loadQuizzes().cancel
    }, [loadQuizzes])

    useEffect(() => {
        return loadAnswers().cancel
    }, [loadAnswers])

    let main_content = null
    if(quizzes.loading) main_content = <div className="ms-3 text-center"><Spinner animation="border" style={{width: "3rem", height: "3rem"}}/></div>
    else if(quizzes.data != null) main_content = <Container>
        {quizzes.data.entities.map((elem) => {
            const answer = {
                loading: answers.loading,
                answer: answers.data == null ? null : answers.data.answers.find(e => e.quizId === elem.properties.id),
                sessionId: answers.data == null ? null : answers.data.sessionId}
            return <Row key={elem.properties.id}><AnswerQuizCard quiz={elem.properties} answer={answer} reload={loadAnswers} notify={notifyAnswerChange}/></Row>
        })}
        {answers.data == null ? null :
            <SockJsClient url={webSocketUri} topics={webSocketTopic(answers.data.sessionId)} onConnect={onConnectionHandler} onMessage={loadQuizzes} ref={refWebSocket} />
        }
    </Container>

    if(main_content == null) main_content = <Fragment>
        <h1>404 - Session not found</h1>
        <Link to="/">Go back</Link>
    </Fragment>

    return(
        <Fragment>
            <Notification problem={quizzes.problem} onClose={onCloseQuizzesHandler}/>
            <h1 className={"text-center mb-5 mt-3"}>Participant: {id}</h1>
            {webSocketConnected === false ?
                <Container>
                    <hr/>
                    <Row>
                        <h4>Connecting with organizer...</h4>
                        <Spinner animation="border" />
                    </Row>
                    <hr/>
                </Container> : null
            }
            {main_content}
        </Fragment>
    )
}