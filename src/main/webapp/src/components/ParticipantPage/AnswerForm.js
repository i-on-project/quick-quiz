import * as React from "react";
import {useCallback, useState} from "react";
import {Button, Form, Spinner} from "react-bootstrap";
import {useParams} from "react-router-dom";
import {buildInputModel} from "../../utils/models/AnswerModel";
import {parse_body, request_no_content} from "../../utils/Request";
import {Notification} from "../Notification";

const uri = '/api/web/v1.0/non_auth/give_answer'
export const AnswerForm = (props) => {

    const {quiz} = props

    const {id} = useParams()
    const size = quiz.answerType === 'SHORT' ? 280 : null

    const [state, setState] = useState({answer: null, answerChoice: null})
    const [button, setButton] = useState({content: "Save changes", disabled: false})
    const [notification, setNotification] = useState(null)

    const onSubmitHandler = useCallback((event) => {
        setButton({content: <Spinner animation="border"/>, disabled: true})
        event.preventDefault();
        const input_model = buildInputModel(id, props.sessionId, props.quiz.id, state)
        const s_func = () => {
            if(props.reload != null) props.reload()
            if(props.notify != null) props.notify()
            props.onClose()
        }
        const f_func = (problem) => setNotification(problem)
        const func_obj = {success: s_func, failed: f_func}
        const request = request_no_content(uri, {method: 'POST', ...parse_body(input_model)}, func_obj)
        request.fetch.then(() => setButton({content: "Save changes", disabled: false}))
    }, [state, props, id])

    const onChoiceClickHandler = useCallback(event => {
        setState((prev) => {return {...prev, answerChoice: event.target.value}})
    }, [])

    const onChangeHandler = useCallback(event => {
        if(size != null && event.target.value.length > size) return
        setState(prev => {return {...prev, answer: event.target.value}})
    }, [size])

    const onCloseHandler = useCallback(() => setNotification(null), [])

    let form_group
    if(quiz.answerType === 'MULTIPLE_CHOICE') form_group = <Form.Group>
        <Form.Label>Choices:</Form.Label>
        {quiz.answerChoices.map((elem, idx) => {
            return <Form.Check name="radio-group" value={elem.choiceNumber} type="radio" key={idx} onChange={onChoiceClickHandler} label={elem.choiceAnswer}/>
        })}
    </Form.Group>
    else form_group = <Form.Group>
        <Form.Label>Answer:</Form.Label>
        <Form.Control as="textarea" rows={size == null ? 7 : 3} placeholder="Answer" type="text" onChange={onChangeHandler}/>
    </Form.Group>


    console.log(state)
    return(
        <Form onSubmit={onSubmitHandler}>
            <Notification problem={notification} onClose={onCloseHandler}/>
            {form_group}
            <Button variant="success" className="mt-3" type="submit"  disabled={button.disabled}>{button.content}</Button>
        </Form>
    )
}