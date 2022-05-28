import {Card, FloatingLabel, Form, ListGroup, Modal, ProgressBar} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import React, {cloneElement, Fragment, useEffect, useState} from "react";
import {QuizAnswerModal} from "../Quizzes/QuizAnswerModal";
import {goPOST} from "../../Services/FetchService";
import SockJsClient from "react-stomp";


export const QuizAnswerCardInSession = (props) => {
    const [show, setShow] = useState(false)
    /**/const [client, setClient] = useState(null)

    const handleClick = () => {
        setShow(true)
    }
    const handleClose = () => setShow(false)
    const hasAnswer = () => props.answer !== undefined &&
        (props.answer.answer !== undefined && props.answer.answer !== null
            || props.answer.answerNumber !== undefined && props.answer.answerNumber !== null)

/*    const sendTestMessage = () => {
        client.sendMessage(`/app/orginsession/${props.data.sessionId}`, JSON.stringify({
            name: 'Test Name',
            message: 'TEst MEssage'
        }));
    }*/

    const saveAnswer = (answer) => {

        const error = (error) => {
            console.log(error)

        }

        const saved = (data) => {
            console.log(data)
            handleClose()
            props.messageOrganizer()
            props.reloadQuizzes()
            //send message to organizer
        }
        goPOST('/api/web/v1.0/non_auth/give_answer', {...answer, guestId: props.answerId}, saved, error)
    }


    return (<Fragment>
        {/*{['md'].map((breakpoint) => (*/}
        <Card style={{width: '16rem'}} className="me-3 mb-3">
            <Card.Title>{props.data.question}</Card.Title>
            <Card.Body>
                <FloatingLabel>{props.data.quizState === 'STARTED' ? "Opened" : "Closed"}</FloatingLabel>
                <FloatingLabel>{hasAnswer() ? "Answered" : "Not Answered"}</FloatingLabel>
            </Card.Body>
            <Card.Footer>
                {props.data.quizState === 'STARTED' &&
                    <Button variant="primary" onClick={() => setShow(true)}
                            className="ms-3 mt-3">{hasAnswer() ? "Edit Answer" : "Answer"}</Button>}
                {props.data.quizState === 'CLOSED' &&
                    <Button variant="primary" disabled={!hasAnswer()} onClick={() => setShow(true)}
                            className="ms-3 mt-3">View Answer</Button>}
            </Card.Footer>
        </Card>


        <Modal show={show}>
            <QuizAnswerModal data={props.data}
                             answer={props.answer}
                             readOnly={props.data.quizState === 'CLOSED'}
                             handleClose={handleClose}
                             handleModalChanges={handleClick}
                             saveAnswer={saveAnswer}
            />
        </Modal>


    </Fragment>)
}