import * as React from "react"
import {Fragment, useCallback, useState} from "react";
import {Modal} from "react-bootstrap";
import {Notification} from "../Notification";
import {QuizForm} from "./QuizForm";
import {parse_body, request_no_content} from "../../utils/Request";

export const NewQuizModal = (props) => {

    const [problem, setProblem] = useState(null)

    const createQuiz = useCallback((input_model) => {
        const s_func = () => {
            props.reload()
            props.onClose()
            if(props.notify != null) props.notify()
        }
        const f_func = (problem) => {
            setProblem(problem)
        }
        const func_obj = {success: s_func, failed: f_func}
        return request_no_content(props.href, {method: 'POST', ...parse_body(input_model)}, func_obj)
    }, [props])

    const onCloseHandler = useCallback(() => {
        setProblem(null)
    }, [])

    return (
        <Fragment>
            <Notification problem={problem} onClose={onCloseHandler}/>
            <Modal.Header closeButton onClick={props.onClose}>
                <Modal.Title>Create a new Quiz</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <QuizForm perform={createQuiz} quiz={{
                    order: '',
                    question: '',
                    questionType: ''
                }} button={{content: "Create"}}/>
            </Modal.Body>
        </Fragment>
    )
}