import * as React from "react"
import {Fragment, useCallback, useState} from "react";
import {Modal} from "react-bootstrap";
import {Notification} from "../Notification";
import {QuizForm} from "./QuizForm";
import {parse_body, request_no_content} from "../../utils/Request";

export const EditQuizModal = (props) => {

    const [problem, setProblem] = useState(null)

    const editQuiz = useCallback((input_model) => {
        const s_func = () => {
            props.reload()
            props.onClose()
            if(props.notify != null) props.notify()
        }
        const f_func = (problem) => {
            setProblem(problem)
        }
        const func_obj = {success: s_func, failed: f_func}
        request_no_content(props.href, {method: 'PUT', ...parse_body(input_model)}, func_obj)
    }, [props])

    const onCloseHandler = useCallback(() => {
        setProblem(null)
    }, [])

    return (
        <Fragment>
            <Notification problem={problem} onClose={onCloseHandler}/>
            <Modal.Header closeButton onClick={props.onClose}>
                <Modal.Title>Edit the Quiz</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <QuizForm perform={editQuiz} quiz={{
                    order: props.quiz.order,
                    choices: props.quiz.answerChoices,
                    question: props.quiz.question,
                    answerType: props.quiz.answerType
                }} button={{content: "Save Changes"}}/>
            </Modal.Body>
        </Fragment>
    )
}