import React, {Fragment, useEffect, useState} from "react";
import {Modal, Button} from "react-bootstrap";
import {ShortQuiz} from "./ShortQuiz";
import {LongQuiz} from "./LongQuiz";

export const QuizModal = ((props) => {
  /*      const [show, setShow] = useState(props.show)*/

        const handleClose = () => props.handleClose();
        const onAnswerHandler = () => console.log('i have an answer!S')
        return (
            <Fragment>
                <Modal.Header closeButton>
                    <Modal.Title>Modal title</Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    {props.data.answerType === 'SHORT' && (<ShortQuiz question={props.data.question} onChangeHandler={onAnswerHandler} />)}
                    {props.data.answerType === 'LONG' && (<LongQuiz question={props.data.question} onChangeHandler={onAnswerHandler} />)}
                    {props.data.answerType === 'MULTIPLE_CHOICE' && (<ShortQuiz question={props.data.question} onChangeHandler={onAnswerHandler} />)}
                </Modal.Body>

                <Modal.Footer>
                    <Button variant="secondary"  onClick={handleClose}>Close</Button>
                    <Button variant="primary">Save changes</Button>
                </Modal.Footer>
            </Fragment>
        )
    }
)
