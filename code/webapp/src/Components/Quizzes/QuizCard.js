import {Card, Container, Form, ListGroup, Modal, Row} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import React, {Fragment, useEffect, useState} from "react";
import {QuizModal} from "./QuizModal";
import {CreateEditQuizModal} from "./CreateEditQuizModal";
import {goDEL, goPOST} from "../../Services/FetchService";


export const QuizCard = (props) => {
    const [show, setShow] = useState(false)
    const [showEdit, setShowEdit] = useState(false)
    const [dataForEdit, setDataForEdit] = useState(null)
    const [status, setStatus] = useState(props.data.quizState)

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

        const setError = (error) => console.log(`Error Updating Quiz: ${id} with error ${error} `)
        const setData = (data) => console.log(`Updated Quiz ${id} Successfully!! Response: ${data}`)

        goPOST(`/api/web/v1.0/auth/quiz/${id}`, quiz, setData, setError, 'PUT')
    }

    const removeQuiz = (id) => {
        const setError = (error) => console.log(`Error Deleting Quiz: ${id} with error ${error} `)
        const setData = (data) => console.log(`Deleted Quiz ${id} Successfully!! Response: ${data}`)
        goDEL(`/api/web/v1.0/auth/quiz/${id}`)
    }

    const updateStatus = (newStatus) => {

         const toUpdate = {
             quizState: newStatus,
         }
        const setError = (error) => {
            if(error !== null)
                console.log(`Error Updating Quiz: ${props.data.id} with error ${error} `)
        }
        const setData = (data) => {
            if(data !== null ) {
                console.log(`Updated Quiz ${props.data.id} Successfully!! Response: ${data}`)
                setStatus(newStatus)
            }
        }

        goPOST(`/api/web/v1.0/auth/quiz/${props.data.id}/updatestatus`, toUpdate, setData, setError, 'PUT')

         console.log(toUpdate)
    }

    return (
        <Fragment>
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
                <ListGroup.Item className="col-1"><Button variant="primary"
                                                          onClick={handleClick}>Edit</Button></ListGroup.Item>
                <ListGroup.Item className="col-1"><Form>
                    <Button variant="primary" type="submit"
                            onClick={() => removeQuiz(props.data.id)}>Remove</Button>
                </Form></ListGroup.Item>

            </ListGroup>

                       {/* ))}*/}
            <Modal show={show}>
                <CreateEditQuizModal data={dataForEdit} handleClose={handleClose} handleModalChanges={handleClick}
                                     updateQuiz={updateQuizHandler}/>
            </Modal>
        </Fragment>
    )
}