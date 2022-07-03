import * as React from "react"
import {Fragment, useCallback, useState} from "react";
import {Modal} from "react-bootstrap";
import {parse_body, request_no_content} from "../../utils/Request";
import {Notification} from "../Notification";
import {TemplateForm} from "./TemplateForm";

export const NewTemplateModal = ({href, reload, onClose}) => {

    const [problem, setProblem] = useState(null)

    const createTemplate = useCallback((input_model) => {
        const s_func = () => { reload(); onClose() }
        const f_func = (problem) => setProblem(problem)
        const func_obj = {success: s_func, failed: f_func}
        request_no_content(href, {method: 'POST', ...parse_body(input_model)}, func_obj)
    }, [href, reload, onClose])

    const onCloseHandler = useCallback(() => setProblem(null), [])

    return (
        <Fragment>
            <Notification problem={problem} onClose={onCloseHandler}/>
            <Modal.Header closeButton onClick={onClose}>
                <Modal.Title>Create a new Template</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <TemplateForm perform={createTemplate} template={{
                    name: '',
                    limitOfParticipants: '',
                    geolocation: '',
                    radius: '',
                    quizzes: []
                }}/>
            </Modal.Body>
        </Fragment>
    )
}