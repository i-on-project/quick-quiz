import {Form, ListGroup, Modal} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import React, {Fragment, useState} from "react";
import {CreateEditQuizModal} from "./CreateEditQuizModal";
import {goDEL, goPOST} from "../../Services/FetchService";


export const QuizCard = (props) => {
    const [show, setShow] = useState(false)
    const [dataForEdit, setDataForEdit] = useState(null)
    const [status, setStatus] = useState(props.data.quizState)
    const [shoAns, setShowAns] = useState(false)

    const handleClick = () => {
        /*props.openSession(props.name, props.link)*/
        console.log(`Data in Quiz Card: ${props.data}`)
        setDataForEdit(props.data)
        setShow(true)
    }
    const handleClose = () => {
        setShow(false)
        setDataForEdit(null)
    }

    const updateQuizHandler = (id, quiz) => {

        const setError = (error) => error !== null ? console.log(`Error Updating Quiz: ${id} with error ${error} `) : null
        const setData = (data) => {
            handleClose()
            props.reloadQuizzes()
        }
        goPOST(props.quizHref, quiz, setData, setError, 'PUT')
    }

    const removeQuiz = (id) => {
        const setError = (error) => {
            console.log(`Error Deleting Quiz: ${id} with error ${error} `)
        }
        const setData = (data) => {
            console.log(`Deleted Quiz ${id} Successfully!! Response: ${data}`)
            props.reloadQuizzes()
        }
        console.log(`Quiz Self: ${props.quizHref}`)
        goDEL(props.quizHref, setData, setError)
    }

    const updateStatus = (newStatus) => {

        const toUpdate = {
            quizState: newStatus,
        }
        const setError = (error) => {
            if (error !== null) console.log(`Error Updating Quiz: ${props.data.id} with error ${error} `)
        }
        const setData = (data) => {
            if (data !== null) {
                console.log(`Updated Quiz ${props.data.id} Successfully!! Response: ${data}`)
                setStatus(newStatus)
            }
        }
        goPOST(`${props.quizHref}/updatestatus`, toUpdate, setData, setError, 'PUT')
    }

    return (<Fragment>
        {/*{['md'].map((breakpoint) => (*/}

        <ListGroup horizontal>
            <ListGroup.Item className="col-4">{props.data.question}</ListGroup.Item>
            <ListGroup.Item className="col-3">{props.data.answerType}</ListGroup.Item>
            <ListGroup.Item className="col-1">{props.data.order}</ListGroup.Item>
            <ListGroup.Item className="col-2">
                <Form.Select value={status}
                             onChange={(e) => updateStatus(e.target.value)}>
                    <option value='NOT_STARTED'>Not Started</option>
                    <option value='STARTED'>Started</option>
                    <option value='CLOSED'>Closed</option>
                </Form.Select>
            </ListGroup.Item>
            {status === 'NOT_STARTED' &&
                <ListGroup.Item>
                    <Button variant="primary" onClick={handleClick}>
                        Edit
                    </Button>
                </ListGroup.Item>}
            {status === 'NOT_STARTED' && <ListGroup.Item>
                <Button variant="primary" type="submit" onClick={() => removeQuiz(props.data.id)}>
                    Remove
                </Button>
            </ListGroup.Item>
            }
            {status !== 'NOT_STARTED' && <ListGroup.Item>
                <Button variant="primary" type="submit" onClick={() => removeQuiz(props.data.id)}>
                    Answers
                </Button>
            </ListGroup.Item>
            }
        </ListGroup>

        <Modal show={show}>
            <CreateEditQuizModal data={dataForEdit}
                                 handleClose={handleClose}
                                 handleModalChanges={handleClick}
                                 updateQuiz={updateQuizHandler}/>
        </Modal>
    </Fragment>)
}