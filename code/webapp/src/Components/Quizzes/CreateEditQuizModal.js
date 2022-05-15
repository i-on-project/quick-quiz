import React, {Fragment, useEffect, useState} from "react";
import {Button, Form, Dropdown, FormControl, InputGroup, Modal, Col, Row} from "react-bootstrap";


export const CreateEditQuizModal = ((props) => {


        const [type, setType] = useState('SHORT')
        const [question, setQuestion] = useState('')
        const [newAnsOptions, setNewAnsOptions] = useState([{"choice": ""}, {"choice": ""}, {"choice": ""}, {"choice": ""}])
        const [validated, setValidated] = useState(false);
        const [order, setOrder] = useState(0)
        const [rightAnswer, setRightAnswer] = useState(-1)

        const handleClose = () => props.handleClose()


        const onTypeChange = (e) => {
            setType(e.target.value)
        }

        const onQuestionUpdate = (e) => {
            setQuestion(e.target.value)
        }

        const fixNewAns = () => {
            const tmp = newAnsOptions.map( (e, i) => (
                {...e, choiceNumber: i+1, choiceRight: i===rightAnswer}
                )
            )
            //setNewAnsOptions([...newAnsOptions, ...tmp])
            return {
                question: question,
                questionType: type,
                order: order,
                choices: tmp
            }
        }

        const handleAdd = () => {
            setNewAnsOptions([...newAnsOptions, {"choice": ""}])
            //setNChoices((c) => c + 1)
        }

        const handleRemoval = () => {
            newAnsOptions.pop()
            setNewAnsOptions([...newAnsOptions])
            //setNChoices((c) => c - 1)
        }

        const handleAnswerChanges = (e, i) => {
            const temp = newAnsOptions.map( (a, index) => index === i ? {"choice": e.target.value} : a)
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
                            defaultValue={a.choice}
                            onChange={(e) => handleAnswerChanges(e, i)}
                        />
                        <InputGroup.Radio name="correct" onChange={() => setRightAnswer(i)} />
                        {i > 3 &&
                            <Button variant="outline-secondary" id="button-addon2" onClick={handleRemoval}>
                                Remove
                            </Button>}
                    </InputGroup>
                )
            )
        )

        const handleSubmit = (event) => {
            const form = event.currentTarget;

            if (form.checkValidity() === false) {
                event.preventDefault();
                event.stopPropagation();
            } else {
                const tmp = fixNewAns()
                props.createQuiz(tmp)
            }
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
                            <Form.Select value={type} onChange={onTypeChange}>
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
                        <Button variant="primary" type="submit">Save changes</Button>
                    </Modal.Footer>
                </Form>
            </Fragment>
        )
    }
)