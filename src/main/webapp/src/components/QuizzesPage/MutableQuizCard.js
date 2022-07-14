import * as React from "react"
import {Fragment, useCallback, useState} from "react";
import {parse_body, request_no_content} from "../../utils/Request";
import {Button, Col, Collapse, Container, Form, ListGroup, Modal, Row, Spinner} from "react-bootstrap";
import {EditQuizModal} from "./EditQuizModal";
import {Notification} from "../Notification";
import {questionTypeMapper, quizStateMapper} from "../../utils/models/QuizModel";
import {ActionButton} from "../ActionButon";
import {QuizAnswersList} from "./QuizAnswersList";

export const MutableQuizCard = ({quiz, answers, reload, href, notify}) => {

    const [state, setState] = useState(quiz.quizStatus)
    const [select, setSelect] = useState(true)
    const [problem, setProblem] = useState(null)
    const [modal, setModal] = useState(false)
    const [collapse, setCollapse] = useState(false)

    const onOpenModalHandler = useCallback(() => setModal(true), [])
    const onCloseModalHandler = useCallback(() => setModal(false), [])
    const onCloseHandler = useCallback(() => setProblem(null), [])
    const onCollapseHandler = useCallback(() => setCollapse((prev) => !prev), [])

    const onChangeStateHandler = useCallback(event => {
        const body = {quizStatus: event.target.value}
        setSelect(false)
        const success_func = () => {
            setState(body.quizStatus)
            setSelect(true)
            if(notify != null) notify()
        }
        const failed_func = (problem) => {
            setSelect(true)
            setProblem(problem)
        }
        const func_obj = {success: success_func, failed: failed_func}
        request_no_content(href.status, {method: 'PUT', ...parse_body(body)}, func_obj)
    }, [href.status, notify])

    const onClickDeleteHandler = useCallback(() => {
        const s_func = () => {
            reload()
            if(notify != null) notify()
        }
        const f_func = (problem) => { setProblem(problem) }
        const func_obj = {success: s_func, failed: f_func}
        return request_no_content(href.self, {method: 'DELETE'}, func_obj).fetch
    }, [reload, href.self, notify])

    let modal_content = null
    if(modal) modal_content = <Modal show={modal}>
        <EditQuizModal quiz={quiz} reload={reload} href={href.self} onClose={onCloseModalHandler} notify={notify}/>
    </Modal>

    let collapse_content = null
    if(collapse) collapse_content = <Collapse in={collapse}>
        <QuizAnswersList quiz={quiz} answers={answers} />
    </Collapse>

    return (
        <Fragment>
            <Notification problem={problem} onClose={onCloseHandler}/>
            <ListGroup horizontal>
                <ListGroup.Item className="col-6">{quiz.question}</ListGroup.Item>
                <ListGroup.Item className="col-2">{questionTypeMapper.find((elem) => elem.key === quiz.answerType).value}</ListGroup.Item>
                <ListGroup.Item className="col-2">
                    {select === false ?
                        <Spinner animation="border" /> :
                        <Form><Form.Select onChange={onChangeStateHandler} defaultValue={state}>
                            {quizStateMapper.map((elem, idx) => {
                                return <option key={idx} value={elem.key}>{elem.value}</option>
                            })}
                        </Form.Select></Form>
                    }
                </ListGroup.Item>
                <Container>
                    <Row>
                        {state === 'NOT_STARTED' ?
                            <Fragment>
                                <Col><Button variant="success" onClick={onOpenModalHandler}>Edit</Button></Col>
                                <Col><ActionButton variant="danger" perform={onClickDeleteHandler} content="-"/></Col>
                            </Fragment> :
                            <Fragment>
                                <Col><Button variant="success" onClick={onCollapseHandler}>Answers {collapse === false ? "⮝" : "⮟"}</Button></Col>
                            </Fragment>
                        }
                    </Row>
                </Container>
            </ListGroup>
            {collapse_content}
            {modal_content}
        </Fragment>
    )
}