import * as React from "react";
import {useCallback, useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {request} from "../../utils/Request";
import {Card, Container, Row} from "react-bootstrap";
import {Notification} from "../Notification";
import {secondsToString} from "../../utils/TimeUtils";
import {HistoryQuiz} from "../HistoryPage/HistoryQuiz";

const uri = (pid, sid) => `/api/web/v1.0/non_auth/history/${pid}/${sid}`
export const ShowResults = () => {

    const {participantId, sessionId} = useParams()

    const [state, setState] = useState({data: null, loading: true, problem: null})

    const onCloseHandler = useCallback(() => setState(prev => { return {...prev, problem: null}}), [])

    useEffect(() => {
        const func_obj = {
            success: (data) => setState(prev => { return {...prev, data: data.properties, loading: false}}),
            failed: (problem) => setState(prev => { return {...prev, problem: problem, loading: false}})
        }
        return request(uri(participantId, sessionId), {method: 'GET'}, func_obj).cancel
    }, [participantId, sessionId])

    console.log(state)

    let main_content = null
    if(state.data != null) main_content = <Row><Card>
        <Card.Title>Date: {secondsToString(state.data.date)}</Card.Title>
        <Card.Body>{state.data.quizzes.map((elem, idx) => {
            return <HistoryQuiz key={idx} quiz={elem}/>
        })}</Card.Body>
    </Card></Row>

    return <Container>
        <Row><Notification problem={state.problem} onClose={onCloseHandler}/></Row>
        {main_content}
    </Container>
}

