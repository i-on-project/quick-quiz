import React, {Fragment, useEffect, useState} from "react";
import {Button, Form, Dropdown, FormControl, InputGroup, Modal, Col, Row} from "react-bootstrap";


export const CreateEditQuizModal = ((props) => {

        const validData = () => props.data !== undefined && props.data !== null
        const validChoices = () => validData() && props.data.answerChoices !== undefined && props.data.answerChoices !== null && props.data.answerType === 'MULTIPLE_CHOICE'
        const emptyChoices = [{"choiceAnswer": ""}, {"choiceAnswer": ""}, {"choiceAnswer": ""}, {"choiceAnswer": ""}]

        const [type, setType] = useState(validData() ? props.data.answerType : 'SHORT')
        const [question, setQuestion] = useState(validData() ? props.data.question : '')
        const [newAnsOptions, setNewAnsOptions] = useState(validChoices() ? [...props.data.answerChoices] : emptyChoices)
        const [validated, setValidated] = useState(false);
        const [order, setOrder] = useState(validData() ? props.data.order : 0)
        const [rightAnswer, setRightAnswer] = useState(-1)

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

        const fixOptions = () => {
            if(type !== 'MULTIPLE_CHOICE') return null

            return newAnsOptions.map((e, i) => (
                {...e, choiceNumber: i, choiceRight: i === rightAnswer}
            ))
        }


        const getNewOrEdited = () => {
            const tmp = fixOptions()
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
            //setNChoices((c) => c + 1)
        }

        const handleRemoval = (index) => {
            if (index === (rightAnswer)) {
                alert('This answer cannot be removed because its configured as the right answer!')
                return
            }

            newAnsOptions.splice(index, 1)
            console.log(`Index: ${index}`)
            console.log(`rightAnswer: ${rightAnswer}`)
            if (rightAnswer > index) {
                console.log(`rightAnswer: ${rightAnswer}`)
                setRightAnswer(r => r - 1)
                console.log(`rightAnswer: ${rightAnswer}`)
            }
            console.log(`rightAnswer: ${rightAnswer}`)

            const tmp = fixOptions()

            setNewAnsOptions([...tmp])
            //setNChoices((c) => c - 1)
        }

        const handleAnswerChanges = (e, i) => {
            const temp = newAnsOptions.map((a, index) => index === i ? {"choiceAnswer": e.target.value} : a)
            setNewAnsOptions([...temp])
        }

        let formKey = 0
        const newChoices = () => (newAnsOptions.map((a, i) =>
                (
                    <InputGroup className="mb-3">
                        <FormControl
                            required
                            key={"q_" + formKey++}
                            type="text"
                            placeholder="Possible answer"
                            value={a.choiceAnswer}
                            onChange={(e) => handleAnswerChanges(e, i)}
                        />
                        <InputGroup.Radio name="correct" defaultChecked={a.choiceRight}
                                          onChange={() => setRightAnswer(i)}/>
                        {i > 3 &&
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