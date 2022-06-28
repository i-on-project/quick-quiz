import * as React from "react";
import {Fragment, useCallback, useEffect, useState} from "react";
import {Button, Card, Container, Modal, Row, Spinner} from "react-bootstrap";
import {Navigate, Link, useParams} from "react-router-dom";
import {Notification} from "../Notification";
import {request} from "../../utils/Request";
import {secondsToString} from "../../utils/TimeUtils";
import {getActionHref, getLinksHref} from "../../utils/SirenJson";
import {ProblemJson} from "../../utils/ProblemJson";
import {NewQuizModal} from "../QuizzesPage/NewQuizModal";
import {EditableSession} from "./EditableSession";
import {QuizCard} from "../QuizzesPage/QuizCard";

const href_unknown_problem = new ProblemJson("InvalidRequest", "The uri to fetch the data is unknown")
const uri = (id) => `/api/web/v1.0/auth/sessions/${id}`
export const Session = () => {

    const {id} = useParams()

    const [state_session, setStateSession] = useState({data: null, loading: true, problem: null})
    const [state_quizzes, setStateQuizzes] = useState({data: null, loading: true, problem: null})
    const [modal, setModal] = useState(false)

    const loadSession = useCallback(() => {
        setStateSession(prev => { return {...prev, loading: true}})

        const failed_func = (problem) => setStateSession((prev) => {
            prev.loading = false
            return {...prev, problem: problem}
        })
        const s_func_session = (data) => {
            setStateSession((prev) => {
                prev.data = data
                prev.loading = false
                return {...prev}
            })
        }
        const func_session = {success: s_func_session, failed: failed_func}
        return request(uri(id), {method: 'GET'}, func_session)
    }, [id])

    const loadQuizzes = useCallback(() => {
        setStateQuizzes(prev => { return {...prev, loading: true}})

        const failed_func = (problem) => setStateQuizzes((prev) => {
            prev.loading = false
            return {...prev, problem: problem}
        })
        if(state_session.data == null) failed_func(href_unknown_problem)
        const href = getLinksHref(state_session.data.links, 'related', 'Quizzes')
        if(href == null) failed_func(href_unknown_problem)

        const s_func_quizzes = (data) => setStateQuizzes((prev) => {
            prev.data = data
            prev.loading = false
            return {...prev}
        })

        const func_quizzes = {success: s_func_quizzes, failed: failed_func}
        return request(href, {method: 'GET'}, func_quizzes)
    }, [state_session.data])

    useEffect(() => { return loadSession().cancel }, [loadSession])
    useEffect(() => {
        if(state_session.data == null) return
        return loadQuizzes().cancel
    }, [state_session.data, loadQuizzes])

    const onCloseSessionAlert = useCallback(() => setStateSession((prev) => { return {...prev, problem: null}}), [])
    const onCloseQuizzesAlert = useCallback(() => setStateQuizzes((prev) => { return {...prev, problem: null}}), [])
    const onCloseModalHandler = useCallback(() => setModal(false), [])
    const onClickModalHandler = useCallback(() => setModal(true), [])

    const sortHandler = useCallback((a, b) => a.properties.order - b.properties.order, [])

    let modal_content = null
    if(modal) modal_content = <Modal show={modal}>
        <NewQuizModal reload={loadQuizzes} href={getActionHref(state_session.data.actions, "Add-Quiz")} onClose={onCloseModalHandler}/>
    </Modal>

    const spinner = <div className="ms-3 text-center"><Spinner animation="border" style={{width: "3rem", height: "3rem"}}/></div>

    let main_content = null
    if(state_session.loading) main_content = spinner
    else {
        let session_content = null
        if(state_session.data != null) {
            const session = state_session.data.properties
            if(session.status === 'STARTED') return <Navigate to={''} />
            session_content = <Container><Row><Card className="mt-3">
                <Card.Body>
                    <Card.Title>{session.name}</Card.Title>
                    <Card.Text>{session.description}</Card.Text>
                    <Card.Text>Created on: {secondsToString(session.creationDate)}</Card.Text>
                    <EditableSession session={session} reload={loadSession} href={getActionHref(state_session.data.actions, "Update-Session")}/>
                </Card.Body>
            </Card></Row></Container>
        }
        let quizzes_content = null
        if(state_quizzes.loading) quizzes_content = spinner
        else if(state_quizzes.data != null && session_content != null) {
            const quizzes = state_quizzes.data.entities
            quizzes_content = <Container fluid="md">
                <hr/>
                <Row><Button variant="success" className="mb-3" onClick={onClickModalHandler}>Add new quiz</Button></Row>
                <Row>{quizzes.sort(sortHandler).map((elem) => {
                    return <QuizCard key={elem.properties.id} quiz={elem.properties} href={elem.href} reload={loadQuizzes} />
                })}</Row>
                {modal_content}
            </Container>
        }
        if(session_content != null) main_content = <Fragment>
            {session_content}
            <Notification problem={state_quizzes.problem} onClose={onCloseQuizzesAlert}/>
            {quizzes_content}
        </Fragment>
    }
    if(main_content == null) main_content = <Fragment>
        <h1>404 - Session not found</h1>
        <Link to="/sessions">Go back</Link>
    </Fragment>

    return(
        <Fragment>
            <Notification problem={state_session.problem} onClose={onCloseSessionAlert}/>
            {main_content}
        </Fragment>
    )
}