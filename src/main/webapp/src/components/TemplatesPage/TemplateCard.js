import * as React from "react"
import {Fragment, useCallback, useState} from "react";
import {Link} from "react-router-dom"
import {Card, Col, Container, Row} from "react-bootstrap";
import {request_no_content} from "../../utils/Request";
import {Notification} from "../Notification";
import {ActionButton} from "../ActionButon";

const uri = (id) => `/api/web/v1.0/auth/template/${id}`
export const TemplateCard = ({template, reload}) => {

    const [problem, setProblem] = useState(null)

    const onCloseHandler = useCallback(() => setProblem(null), [])

    const onClickDeleteHandler = useCallback(() => {
        const s_func = () => reload()
        const f_func = (problem) => setProblem(problem)
        const func_obj = {success: s_func, failed: f_func}
        return request_no_content(uri(template.id), {method: 'DELETE'}, func_obj).fetch
    }, [reload, template.id])

    return (
        <Fragment>
            <Notification problem={problem} onClose={onCloseHandler}/>
            {/*<Card style={{width: '20rem'}} className="mb-3">*/}
            <Card className="mb-3">
                <Card.Body><Container><Row>
                    <Col className="col-11"><h3><Link to={`/template/${template.id}`}>{template.id}</Link></h3></Col>
                    <Col className="col-1"><ActionButton variant="danger" perform={onClickDeleteHandler} content="Delete"/></Col>
                </Row></Container></Card.Body>
            </Card>
        </Fragment>
    )
}