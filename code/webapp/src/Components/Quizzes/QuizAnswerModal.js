import React, {Fragment, useReducer, useState} from "react";
import {Button, Form, FormControl, FormLabel, InputGroup, Modal} from "react-bootstrap";

export const QuizAnswerModal = (props) => {

    const hasAnswer = () => props.answer !== undefined

    const [validated, setValidated] = useState(false)

    const [answer, setAnswer] = useState(!hasAnswer() || props.answer.answer === undefined
        ? null : props.answer.answer)
    const [answerChoice, setAnswerChoice] = useState(!hasAnswer() || props.answer.answerNumber === undefined
        ? null : props.answer.answerNumber)


    const MultiChoiceView = (choice, index) => (
        <Fragment>
            <InputGroup className="mb-3">
                <InputGroup.Radio name="answer"
                                  checked={index === answerChoice}
                                  onChange={() => setAnswerChoice(index)}/>
                <FormLabel className="ms-3">{choice.choiceAnswer}</FormLabel>
            </InputGroup>
        </Fragment>
    )

    const ShortView = () => (
        <Fragment>
            <InputGroup className="mb-3">
                <FormControl
                    key={"short_answer"}
                    placeholder="Write a short answer here"
                    aria-label="Order in session"
                    aria-describedby="order"
                    type="text"
                    value={answer}
                    onChange={(e) => setAnswer(e.target.value)}
                />
            </InputGroup>
        </Fragment>
    )


    const LongView = () => (
        <Fragment>
            <InputGroup className="mb-3">
                <FormControl
                    as="textarea" rows={5}
                    key={"long_answer"}
                    placeholder="Write your answer here"
                    aria-label="Order in session"
                    aria-describedby="order"
                    type="text"
                    value={answer}
                    onChange={setAnswer}
                />
            </InputGroup>
        </Fragment>
    )


    const handleClose = () => props.handleClose()

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        event.preventDefault();
        if (form.checkValidity() === false) {
            event.stopPropagation();
        } else {

            const ans = {
                sessionId: props.data.sessionId,
                quizId: props.data.id,
                answer: answer,
                answerChoice: answerChoice
            }
            props.saveAnswer(ans)
        }
        setValidated(true)
    };


    return (
        <Fragment>
            <Form noValidate validated={validated} onSubmit={handleSubmit}>
                <Modal.Header closeButton onClick={handleClose}>
                    <Modal.Title>{props.data.question}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {props.data.answerType === "MULTIPLE_CHOICE" && props.data.answerChoices.map((c, index) => MultiChoiceView(c, index))}
                    {props.data.answerType === "SHORT" && ShortView()}
                    {props.data.answerType === "LONG" && LongView()}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>Close</Button>
                    {!props.readOnly && <Button variant="primary" type="submit">Save</Button>}
                </Modal.Footer>
            </Form>
        </Fragment>
    )
}