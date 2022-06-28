import * as React from "react"
import {Fragment, useCallback, useState} from "react";
import {Button, Form, FormControl, InputGroup, Spinner} from "react-bootstrap";
import {buildInputModel, questionTypeMapper, validateInputModel} from "../../utils/QuizModel";
import {stringOnlyDigits} from "../../utils/StringUtils";

function nullToEmptyStr(quiz) {
    return {
        questionType: quiz.questionType === '' ? questionTypeMapper[0].key : quiz.questionType,
        order: quiz.order == null ? '' : quiz.order.toString(),
        question: quiz.question,
        choices: quiz.choices
    }
}

const choiceModel = {choiceRight: false, choiceAnswer: ''}

export const QuizForm = (props) => {

    const {perform, quiz} = props

    const [state, setState] = useState({quiz: {...nullToEmptyStr(quiz)}})

    let button_initial_state
    if(props.button != null) {
        button_initial_state = {
            content: props.button.content == null ? 'Submit' : props.button.content,
            className: props.button.className == null ? 'mt-3' : props.button.className,
            variant: props.button.variant == null ? 'success' : props.button.variant,
            disabled: false
        }
    } else button_initial_state = {content: 'Submit', disabled: false}

    const [button, setButton] = useState(button_initial_state)

    const onSubmitHandler = useCallback((event) => {
        setButton({content: <Spinner animation="border"/>, disabled: true})
        try {
            const model = quiz.answerType != null ? {...state.quiz, questionType: quiz.answerType} : state.quiz
            event.preventDefault();
            const warnings = validateInputModel(model)
            if (warnings.length !== 0) setState((prev) => {
                prev.warnings = warnings;
                return {...prev}
            })
            else {
                if (perform == null) return
                const input_model = buildInputModel(state.quiz)
                perform(input_model)
            }
        } finally {
            setButton(button_initial_state)
        }
    }, [state, quiz, perform, button_initial_state])

    const onChangeTypeHandler = useCallback((event) => {
        setState((prev) => {
            prev.quiz.questionType = event.target.value
            if(event.target.value === 'MULTIPLE_CHOICE') prev.quiz.choices = [
                {...choiceModel},
                {...choiceModel},
            ]
            else prev.quiz.choices = null
            return {...prev}
        })
    }, [])

    const onChangeOrderHandler = useCallback((event) => {
        setState((prev) => {
            prev.quiz.order = stringOnlyDigits(event.target.value)
            return {...prev}
        })
    }, [])

    const onChangeQuestionHandler = useCallback((event) => {
        if(event.target.value.length > 250) return
        setState((prev) => {
            prev.quiz.question = event.target.value
            return {...prev}
        })
    }, [])

    const onChangeChoiceHandler = useCallback((event, idx) => {
        if(event.target.value.length > 150) return
        setState((prev) => {
            prev.quiz.choices[idx].choiceAnswer = event.target.value
            return {...prev}
        })
    }, [])

    const onChoiceClickHandler = useCallback((event, idx) => {
        setState((prev) => {
            prev.quiz.choices[idx].choiceRight = event.target.checked
            return {...prev}
        })
    }, [])

    const onClickHandler = useCallback(() => {
        setState((prev) => {
            prev.quiz.choices.push({...choiceModel})
            return {...prev}
        })
    }, [])

    const onClickRemoveHandler = useCallback((idx) => {
        setState((prev) => {
            prev.quiz.choices.splice(idx, 1)
            return {...prev}
        })
    }, [])

    let question_warnings = []
    let type_warnings = []
    let order_warnings = []
    let choices_warnings = []
    if(state.warnings != null) {
        state.warnings.forEach((elem, idx) => {
            switch (elem.value) {
                case 'question': question_warnings.push(<Form.Text key={idx} className="text-danger">{elem.message}</Form.Text>); break;
                case 'order': order_warnings.push(<Form.Text key={idx} className="text-danger">{elem.message}</Form.Text>); break;
                case 'questionType': type_warnings.push(<Form.Text key={idx} className="text-danger">{elem.message}</Form.Text>); break;
                case 'choices': choices_warnings.push(<Fragment key={idx}><Form.Text className="text-danger">{elem.message}</Form.Text><br/></Fragment>); break;
                default: break;
            }
        })
    }

    let choices_form = null
    if(state.quiz.choices != null) choices_form = <Form.Group>
        <Form.Label>Choices:</Form.Label>
        {state.quiz.choices.map((elem, idx) => {
            return <InputGroup key={idx}>
                <FormControl placeholder="Answer" type="text" value={elem.choiceAnswer} onChange={(event) => onChangeChoiceHandler(event, idx)} />
                <InputGroup.Checkbox checked={elem.choiceRight} type="checkbox" onChange={(event) => onChoiceClickHandler(event, idx)} />
                {
                    state.quiz.choices.length <= 2 ? null :
                    <Button variant="danger" onClick={() => {onClickRemoveHandler(idx)}}>-</Button>
                }
            </InputGroup>
        })}
        {choices_warnings}
        <Button variant="secondary" className="mt-3" onClick={onClickHandler}>Add Choice</Button>
    </Form.Group>

    return(
        <Form onSubmit={onSubmitHandler}>
            {quiz.questionType == null ? null : <Form.Group>
                <Form.Label>Quiz Answer Type:</Form.Label>
                <Form.Select onChange={onChangeTypeHandler}>
                    {questionTypeMapper.map((elem, idx) => {
                        return <option key={idx} value={elem.key}>{elem.value}</option>
                    })}
                </Form.Select>
                {type_warnings}
            </Form.Group>}
            {quiz.order == null ? null : <Form.Group>
                <Form.Label>Question Number:</Form.Label>
                <Form.Control placeholder="Order" type="text" maxLength="2"
                              value={state.quiz.order} onChange={onChangeOrderHandler}/>
                {order_warnings}
            </Form.Group>}
            {quiz.question == null ? null : <Form.Group>
                <Form.Label>Question:</Form.Label>
                <Form.Control placeholder="Question?" type="text" value={state.quiz.question} onChange={onChangeQuestionHandler}/>
                {question_warnings}
            </Form.Group>}
            {choices_form}
            <Button variant={button.variant} type='submit' disabled={button.disabled} onClick={onSubmitHandler} className={button.className}>{button.content}</Button>
        </Form>
    )
}