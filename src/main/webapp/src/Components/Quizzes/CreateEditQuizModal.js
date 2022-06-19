import React, {Fragment, useEffect, useReducer, useState} from "react";
import {Button, Form, Dropdown, FormControl, InputGroup, Modal, Col, Row} from "react-bootstrap";

function reducer(state, action) {
    switch (action.type) {
        case 'set':
            state.data.rightAnswer = action.payload.rightAnswer
            return action.payload.rightAnswer
        case 'increment':
            return state.data.rightAnswer += 1
        case 'decrement':
            return state.data.rightAnswer -= 1;
        default:
            throw new Error();
    }
}


export const CreateEditQuizModal = ((props) => {

        const validData = () => props.data !== undefined && props.data !== null
        const validChoices = () => validData() && props.data.answerChoices !== undefined && props.data.answerChoices !== null && props.data.answerType === 'MULTIPLE_CHOICE'
        const emptyChoices = [{"choiceAnswer": ""}, {"choiceAnswer": ""}]

        const [type, setType] = useState(validData() ? props.data.answerType : 'SHORT')
        const [question, setQuestion] = useState(validData() ? props.data.question : '')
        const [newAnsOptions, setNewAnsOptions] = useState(validChoices() ? [...props.data.answerChoices] : emptyChoices)
        const [validated, setValidated] = useState(false);
        const [order, setOrder] = useState(validData() ? props.data.order : 0)
        const [rightAnswer, setRightAnswer] = useState(-1)
        const [state, dispatch] = useReducer(reducer,  {
            loading: false,
            data: {rightAnswer: 0},
            error: undefined
        });

        const handleClose = () => props.handleClose()

        useEffect(() => {
            if (validChoices())
                setRightAnswer(props.data.answerChoices.find(a => a.choiceRight === true).choiceNumber)
        }, [props.data])

        const onTypeChange = (e) => {
            setType(e.target.value)
        }

        const onQuestionUpdate = (e) => {
            setQuestion(e.target.value)
        }

        const fixOptions = (right) => {
            if(type !== 'MULTIPLE_CHOICE') return null

            const tmp = newAnsOptions.map((e, i) => (
                {...e, choiceNumber: i, choiceRight: i === right}
            ))
            return tmp
        }

        const onRightAnsChange = (index) => {
            setRightAnswer(index)
            const tmp = fixOptions(index)
            setNewAnsOptions([...tmp])
        }

        const getNewOrEdited = () => {
            const tmp = fixOptions(rightAnswer)
            //setNewAnsOptions([...newAnsOptions, ...tmp])
            return !validData() ? {
                question: question,
                questionType: type,
                order: order,
                choices: tmp
            } : {
                question: question,
                order: order,
                choices: tmp
            }
        }

        const handleAdd = () => {
            setNewAnsOptions([...newAnsOptions, {"choiceAnswer": ""}])
        }

        const handleRemoval = (index) => {
            if (index === (rightAnswer)) {
                alert('This answer cannot be removed because its configured as the right answer!')
                return
            }

            let tmp = []
            if (rightAnswer > index) {
                setRightAnswer(r => r - 1)
                tmp = fixOptions(rightAnswer)
            }
            else
                tmp = [...newAnsOptions]
            console.log(tmp)
            tmp.splice(index, 1)
            setNewAnsOptions([...tmp])
        }

        const handleAnswerChanges = (e, i) => {
            const temp = newAnsOptions.map((a, index) => index === i ? {"choiceAnswer": e.target.value} : a)
            setNewAnsOptions([...temp])
        }


        const newChoices = () => (
            newAnsOptions.map((a, i) =>
                (
                    <InputGroup className="mb-3">
                        <FormControl
                            required
                            key={"q_" + i}
                            type="text"
                            placeholder="Possible answer"
                            value={a.choiceAnswer}
                            onChange={(e) => handleAnswerChanges(e, i)}
                        />
                        <InputGroup.Radio name="correct"
                                          checked={a.choiceRight}
                                          onChange={() => onRightAnsChange(i)}/>
                        {newAnsOptions.length > 2 &&
                            <Button variant="outline-secondary" id="button-addon2" onClick={() => handleRemoval(i)}>
                                Remove
                            </Button>}
                    </InputGroup>
                )
            )
        )

        const handleSubmit = (event) => {
            const form = event.currentTarget;
            //event.preventDefault();
            if (form.checkValidity() === false) {
                event.stopPropagation();
            } else {
                const tmp = getNewOrEdited()
                //console.log(tmp)
                validData() ? props.updateQuiz(props.data.id, tmp) : props.createQuiz(tmp)
            }
            event.preventDefault();
            setValidated(true)
        };


        return (
            <Fragment>
                <Form noValidate validated={validated} onSubmit={handleSubmit}>
                    <Modal.Header closeButton onClick={handleClose}>
                        <Modal.Title>QUIZ</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form.Group className="mb-3">
                            <Form.Label>Answer Type:</Form.Label>
                            <Form.Select value={type} onChange={onTypeChange} disabled={validData()}>
                                <option value='SHORT'>Short</option>
                                <option value='LONG'>Long</option>
                                <option value='MULTIPLE_CHOICE'>Multi-Choice</option>
                            </Form.Select>
                            <Form.Label>Order in session:</Form.Label>
                            <InputGroup>
                                <FormControl
                                    key={"order_number"}
                                    placeholder="Order Number?"
                                    aria-label="Order in session"
                                    aria-describedby="order"
                                    type="number"
                                    value={order}
                                    onChange={(e) => setOrder(e.target.value)}
                                />
                            </InputGroup>
                            <Form.Label>Question:</Form.Label>
                            <InputGroup>
                                <FormControl
                                    key={"question_input"}
                                    placeholder="Question?"
                                    aria-label="A Question"
                                    aria-describedby="question"
                                    value={question}
                                    onChange={onQuestionUpdate}
                                />
                            </InputGroup>

                            {type === 'MULTIPLE_CHOICE' && <Form.Label>Possible Answers:</Form.Label>}
                            {type === 'MULTIPLE_CHOICE' && newChoices()}

                        </Form.Group>

                        {type === 'MULTIPLE_CHOICE' && (<Button variant="primary" onClick={handleAdd}>Add</Button>)}

                    </Modal.Body>

                    <Modal.Footer>
                        <Button variant="secondary" onClick={handleClose}>Close</Button>
                        <Button variant="primary" type="submit">Save</Button>
                    </Modal.Footer>
                </Form>
            </Fragment>
        )
    }
)