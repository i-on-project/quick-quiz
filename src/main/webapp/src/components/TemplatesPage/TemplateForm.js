import * as React from "react";
import {Fragment, useCallback, useState} from "react";
import {Button, Form, FormControl, FormSelect, InputGroup, Spinner} from "react-bootstrap";
import {getCurrentLocation} from "../../utils/LocationUtils";
import {stringOnlyDigits} from "../../utils/StringUtils";
import {buildInputModel, validateInputModel} from "../../utils/models/TemplateModel";
import {questionTypeMapper} from "../../utils/models/QuizModel";

function nullToEmptyStr(template) {
    return {
        name: template.name == null ? '' : template.name,
        limitOfParticipants: template.limitOfParticipants.toString(),
        geolocation: template.geolocation == null ? '' : template.geolocation,
        radius: template.radius == null ? '' : template.radius.toString(),
        quizzes: template.quizzes
    }
}

const choice_template = {
    choiceAnswer: '',
    choiceRight: false
}

const quiz_template = {
    order: '',
    question: '',
    answerType: questionTypeMapper[0].key,
    answerChoices: []
}

export const TemplateForm = (props) => {

    const {perform} = props
    const {template} = props
    const {clear} = props

    const [state, setState] = useState({template: {...nullToEmptyStr(template)}})
    const [button, setButton] = useState({content: 'Submit', disabled: false})

    const clearTemplateState = useCallback(() => {
        setState({template: {...nullToEmptyStr(template)}})
        if(clear != null) clear()
    }, [template, clear])

    const onSubmitHandler = useCallback((event) => {
        event.preventDefault();
        const warnings = validateInputModel(state.template)
        if (warnings.length !== 0) setState((prev) => {
            prev.warnings = warnings;
            return {...prev}
        })
        else {
            setButton({content: <Spinner animation="border"/>, disabled: true})
            if(perform == null) return
            const input_model = buildInputModel(state.template)
            perform(input_model)
            setButton({content: 'Submit', disabled: false})
        }
    }, [state.template, perform])

    const onClickUseLocation = useCallback(() => {
        const s_func = (location) => {
            const {latitude, longitude, accuracy} = location.coords;
            setState((prev) => {
                prev.template.geolocation = `${latitude},${longitude},${accuracy}`
                return {...prev}
            })
        }

        const f_func = (error) => {
            alert(`Couldn't use the device location\nError: ${error.toString()}`)
        }

        getCurrentLocation(s_func, f_func)
    }, [])

    const onClickRemoveLocation = useCallback(() => {
        setState((prev) => {
            prev.template.geolocation = ''
            return {...prev}
        })
    }, [])

    const onChangeNameHandler = useCallback((event) => {
        if(event.target.value.length > 50) return
        setState((prev) => {
            prev.template.name = event.target.value
            return {...prev}
        })
    }, [])

    const onChangeParticipantsHandler = useCallback((event) => {
        setState((prev) => {
            prev.template.limitOfParticipants = stringOnlyDigits(event.target.value)
            return {...prev}
        })
    }, [])

    const onChangeRadiusHandler = useCallback((event) => {
        setState((prev) => {
            prev.template.radius = stringOnlyDigits(event.target.value)
            return {...prev}
        })
    }, [])

    const onClickAddQuiz = useCallback(() => {
        setState((prev) => {
            prev.template.quizzes.push({...quiz_template})
            return {...prev}
        })
    }, [])

    const onClickRemoveQuiz = useCallback((idx) => {
        setState((prev) => {
            prev.template.quizzes.splice(idx, 1)
            if(prev.warnings != null) prev.warnings = null
            return {...prev}
        })
    }, [])

    const onChangeQuizQuestion = useCallback((event, idx) => {
        if(event.target.value.length > 250) return
        setState((prev) => {
            prev.template.quizzes[idx].question = event.target.value
            return {...prev}
        })
    }, [])

    const onChangeQuizType = useCallback((event, idx) => {
        setState((prev) => {
            const type = event.target.value
            prev.template.quizzes[idx].answerType = type
            if(type === 'MULTIPLE_CHOICE') prev.template.quizzes[idx].answerChoices = [{...choice_template}, {...choice_template}]
            else prev.template.quizzes[idx].answerChoices = []
            return {...prev}
        })
    }, [])

    const onChangeChoiceHandler = useCallback((event, quiz_idx, answer_idx) => {
        if(event.target.value.length > 150) return
        setState((prev) => {
            prev.template.quizzes[quiz_idx].answerChoices[answer_idx].choiceAnswer = event.target.value
            return {...prev}
        })
    }, [])

    const onChoiceClickHandler = useCallback((event, quiz_idx, answer_idx) => {
        setState((prev) => {
            prev.template.quizzes[quiz_idx].answerChoices[answer_idx].choiceRight = event.target.checked
            return {...prev}
        })
    }, [])

    const onClickRemoveChoice = useCallback((quiz_idx, answer_idx) => {
        setState(prev => {
            prev.template.quizzes[quiz_idx].answerChoices.splice(answer_idx, 1)
            return {...prev}
        })
    }, [])

    const onClickAddAnswer = useCallback(idx => {
        setState(prev => {
            prev.template.quizzes[idx].answerChoices.push({...choice_template})
            return {...prev}
        })
    }, [])

    let name_warnings = []
    let limit_participants_warnings = []
    let radius_warnings = []
    let question_warnings = null
    let answerChoices_warnings = null
    if(state.warnings != null) {
        answerChoices_warnings = Array(state.template.quizzes.length).fill(null)
        question_warnings = Array(state.template.quizzes.length).fill(null)
        state.warnings.forEach((elem, idx) => {
            switch (elem.value) {
                case 'name': name_warnings.push(<Form.Text key={idx} className="text-danger">{elem.message}</Form.Text>); break
                case 'limitOfParticipants': limit_participants_warnings.push(<Form.Text key={idx} className="text-danger">{elem.message}</Form.Text>); break;
                case 'radius': radius_warnings.push(<Form.Text key={idx} className="text-danger">{elem.message}</Form.Text>); break;
                case 'question': {
                    if(question_warnings[elem.idx] == null) question_warnings[elem.idx] = []
                    question_warnings[elem.idx].push(<Form.Text key={idx} className="text-danger">{elem.message}</Form.Text>);
                    break;
                }
                case 'answerChoices': {
                    if(answerChoices_warnings[elem.idx] == null) answerChoices_warnings[elem.idx] = []
                    answerChoices_warnings[elem.idx].push(<Form.Text key={idx} className="text-danger">{elem.message}<br/></Form.Text>)
                    break
                }
                default: break;
            }
        })
    }

    let radius_form = null
    if(state.template.geolocation !== '') radius_form = <Form.Group>
        <Form.Label className="mb-2 mt-3">Radius (meters):</Form.Label>
        <Form.Control type="text" placeholder="Radius (min 50m)" value={state.template.radius} maxLength="9" onChange={onChangeRadiusHandler}/>
        {radius_warnings}
    </Form.Group>

    return (
        <Form onSubmit={onSubmitHandler}>
            {template.name == null ? null : <Form.Group>
                <Form.Label>Name:</Form.Label>
                <Form.Control placeholder="Name" type="text" value={state.template.name} onChange={onChangeNameHandler}/>
                {name_warnings}
            </Form.Group>}
            {template.limitOfParticipants == null ? null : <Form.Group>
                <Form.Label>Limit of Participants:</Form.Label>
                <Form.Control placeholder="Limit of Participants" type="text" maxLength="3"
                              value={state.template.limitOfParticipants} onChange={onChangeParticipantsHandler}/>
                {limit_participants_warnings}
            </Form.Group>}
            {template.geolocation == null ? null : <Form.Group>
                <Form.Label className="mb-2 mt-3">Geolocation:</Form.Label>
                <Form.Control type="text" placeholder="Add Geolocation (optional)"
                              value={state.template.geolocation} readOnly/>
                {state.template.geolocation === '' ?
                    <Button variant="outline-secondary" onClick={onClickUseLocation}>Use geolocation</Button> :
                    <Button variant="outline-secondary" onClick={onClickRemoveLocation}>Remove geolocation</Button>}
            </Form.Group>}
            {radius_form}
            {template.quizzes == null ? null : <Form.Group>
                <Form.Label className="mt-3">Quizzes:</Form.Label>
                {state.template.quizzes.length === 0 ? null : state.template.quizzes.map((elem, idx) => {
                    return <Fragment key={idx}>
                        <InputGroup className="mt-3">
                            <FormSelect value={elem.answerType} className="w-10"
                                        onChange={e => onChangeQuizType(e, idx)}>
                                {questionTypeMapper.map((elem, idx) => {
                                    return <option key={idx} value={elem.key}>{elem.value}</option>
                                })}
                            </FormSelect>
                            <Button variant='danger' onClick={() => onClickRemoveQuiz(idx)}>X</Button>
                        </InputGroup>
                        <InputGroup>
                            <FormControl placeholder="Question?" type="text" value={elem.question}
                                         onChange={e => onChangeQuizQuestion(e, idx)}/>
                            {elem.answerType !== 'MULTIPLE_CHOICE' ? null :
                                <Button variant="secondary" onClick={() => onClickAddAnswer(idx)}>+</Button>
                            }
                        </InputGroup>
                        {elem.answerType === 'MULTIPLE_CHOICE' ? null : question_warnings == null ? null : question_warnings[idx]}
                        {elem.answerType !== 'MULTIPLE_CHOICE' ? null : elem.answerChoices.map((elem, index) =>
                            <InputGroup key={index}>
                                <FormControl placeholder="Answer" type="text" value={elem.choiceAnswer}
                                             onChange={(event) => onChangeChoiceHandler(event, idx, index)}/>
                                <InputGroup.Checkbox checked={elem.choiceRight}
                                                     onChange={(event) => onChoiceClickHandler(event, idx, index)}/>
                                {
                                    state.template.quizzes[idx].answerChoices.length <= 2 ? null :
                                        <Button variant="danger" onClick={() => {
                                            onClickRemoveChoice(idx, index)
                                        }}>-</Button>
                                }
                            </InputGroup>
                        )}
                        {answerChoices_warnings == null ? null : answerChoices_warnings[idx]}
                        {elem.answerType !== 'MULTIPLE_CHOICE' ? null : question_warnings == null ? null : question_warnings[idx]}
                    </Fragment>
                })}
                <br/>
                <Button variant="secondary" onClick={onClickAddQuiz}>Add Quiz</Button>
            </Form.Group>}
            {clear == null ? null :
                <Button variant='success' className='mt-3' onClick={clearTemplateState}>Close</Button>
            }
            <Button variant='success' className='mt-3' type='submit' disabled={button.disabled}>{button.content}</Button>
        </Form>
    )
}