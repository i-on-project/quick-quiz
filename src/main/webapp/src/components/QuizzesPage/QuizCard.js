import * as React from "react";
import {Fragment, useCallback, useState} from "react";
import {Button, Col, Container, ListGroup, Modal, Row} from "react-bootstrap";
import {questionTypeMapper} from "../../utils/QuizModel";
import {EditQuizModal} from "./EditQuizModal";
import {Notification} from "../Notification";
import {request_no_content} from "../../utils/Request";
import {ActionButton} from "../ActionButon";

export const QuizCard = ({quiz, reload, href}) => {

    const [problem, setProblem] = useState(null)
    const [modal, setModal] = useState(false)

    const onOpenModalHandler = useCallback(() => setModal(true), [])
    const onCloseModalHandler = useCallback(() => setModal(false), [])
    const onCloseHandler = useCallback(() => setProblem(null), [])

    const onClickDeleteHandler = useCallback(() => {
        const s_func = () => { reload() }
        const f_func = (problem) => { setProblem(problem) }
        const func_obj = {success: s_func, failed: f_func}
        return request_no_content(href, {method: 'DELETE'}, func_obj).fetch
    }, [reload, href])

    let modal_content = null
    if(modal) modal_content = <Modal show={modal}>
        <EditQuizModal quiz={quiz} reload={reload} href={href} onClose={onCloseModalHandler}/>
    </Modal>

    return (
        <Fragment>
            <Notification problem={problem} onClose={onCloseHandler}/>
            <ListGroup horizontal>
                <ListGroup.Item className="col-8">{quiz.question}</ListGroup.Item>
                <ListGroup.Item className="col-2">{questionTypeMapper.find((elem) => elem.key === quiz.answerType).value}</ListGroup.Item>
                <Container>
                    <Row>
                        <Col><Button variant="success" onClick={onOpenModalHandler}>Edit</Button></Col>
                        <Col><ActionButton variant="danger" perform={onClickDeleteHandler} content="-"/></Col>
                    </Row>
                </Container>
            </ListGroup>
            {modal_content}
        </Fragment>
    )
}