import * as React from "react";
import {Fragment, useCallback, useEffect, useState} from "react";
import {Card, Container, Row, Spinner} from "react-bootstrap";
import {Notification} from "../Notification";
import {useParams} from "react-router-dom";
import {request} from "../../utils/Request";
import {getLinksHref} from "../../utils/SirenJson";
import {ProblemJson} from "../../utils/ProblemJson";

const href_unknown_problem = new ProblemJson("InvalidRequest", "The uri to fetch the data is unknown")
const uri = (id) => `/api/web/v1.0/auth/sessions/${id}`
export const LiveSession = () => {

    const {id} = useParams()
    const [session_state, setSessionState] = useState({data: null, loading: true, problem: null})
    const [quizzes_state, setQuizzesState] = useState({data: null, loading: true, problem: null})
    const [answers_state, setAnswersState] = useState({data: null, loading: true, problem: null})

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

    }, [state.href])

    useEffect(() => {
        const s_func = (data) => {
            const answers_href = getLinksHref(data.links, 'related', 'Answers')
            const quizzes_href = getLinksHref(data.links, 'related', 'Answers')
            setState((prev) => {
                prev.href.answers = answers_href
                prev.href.quizzes = quizzes_href
                prev.loading = false

                return {...prev,
                href: href,
                loading: false,
                session: data.properties
            }})
        }
        const f_func = (problem) => {
            setState((prev) => { return {...prev, loading: false, problem: problem}})
        }
        const func_obj = {success: s_func, failed: f_func}
        return request(uri(id), {method: 'GET'}, func_obj).cancel
    }, [id])

    const onCloseSessionHandler = useCallback(() => {setState(prev => {return{...prev, problem: null}})}, [])

    let main_content = null
    if(state.loading) return <div className="ms-3 text-center"><Spinner animation="border" style={{width: "3rem", height: "3rem"}}/></div>

    return(
        <Fragment>
            <Notification problem={session_state.problem} onClose={onCloseSessionHandler}/>
            <Container><Row>
                <Card className={"mt-3"}>
                    <Card.Title>Session name: {session.name}</Card.Title>
                    <Card.Title>Session description: {session.description}</Card.Title>
                    <Card.Title>Session guest code: {session.guestCode}</Card.Title>
                </Card>
            </Row></Container>
        </Fragment>
    )
}