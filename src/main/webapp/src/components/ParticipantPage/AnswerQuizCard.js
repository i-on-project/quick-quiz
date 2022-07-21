import * as React from "react";
import {useCallback, useState} from "react";
import {Button, Card, Modal, Spinner} from "react-bootstrap";
import {quizStateMapper} from "../../utils/models/QuizModel";
import {AnswerForm} from "./AnswerForm";

export const AnswerQuizCard = ({quiz, answer, reload, notify}) => {

    console.log(quiz)
    const [modal, setModal] = useState(false)

    const onClickHandler = useCallback(() => setModal(true), [])
    const onCloseHandler = useCallback(() => setModal(false), [])

    let card_body = null
    if(answer.loading === true) card_body = <Spinner animation="border" />
    else if (answer.answer == null) card_body = 'Not answered yet!'
    else {
        if(quiz.answerType === 'MULTIPLE_CHOICE') card_body = `Option: ${answer.answer.answerNumber}`
        else card_body = answer.answer.answer
    }

    let button_content = null
    const content = answer.answer == null ? 'Answer' : 'Edit answer'
    if(answer.sessionId == null) button_content = <Button><Spinner animation="border"/></Button>
    else if(quiz.quizStatus === 'STARTED') button_content = <Card.Body>
            <Button onClick={onClickHandler}>{content}</Button>
            <Modal show={modal}>
                <Modal.Header closeButton onClick={onCloseHandler}>
                    <Modal.Title>{content}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <AnswerForm onClose={onCloseHandler} quiz={quiz} sessionId={answer.sessionId} reload={reload} notify={notify}/>
                </Modal.Body>
            </Modal>
        </Card.Body>
    else button_content = <Button disabled={true}>{content}</Button>

    return(
        <Card>
            <Card.Title>State: {quizStateMapper.find(elem => elem.key === quiz.quizStatus).value}</Card.Title>
            <Card.Title>Question: {quiz.question}</Card.Title>
            <Card.Body>Answer: {card_body}</Card.Body>
            {button_content}
        </Card>
    )
}