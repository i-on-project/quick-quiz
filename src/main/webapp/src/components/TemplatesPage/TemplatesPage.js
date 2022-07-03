import * as React from "react"
import {useCallback, useEffect, useState} from "react";
import {Button, Col, Container, Modal, Row, Spinner} from "react-bootstrap";
import {Notification} from "../Notification";
import {request} from "../../utils/Request";
import {TemplateCard} from "./TemplateCard";
import {NewTemplateModal} from "./NewTemplateModal";

const page_size = 10
const uri = '/api/web/v1.0/auth/template'
const url = (page) => `${uri}?page=${page}`
export const Templates = () => {

    const [state, setState] = useState({data: null, loading: true, problem: null})
    const [pages, setPages] = useState({page: 0, totalPages: 1})
    const [modal, setModal] = useState(false)

    const loadPage = useCallback(() => {
        setState((prev) => { return {...prev, loading: true}})
        const s_func = (data) => {
            setState((prev) => { return {...prev,
                loading: false,
                data: data,
                problem: null
            }})
            let total = Math.floor(data.properties.total / page_size) + 1
            if(data.properties.total % page_size === 0) total -= 1
            setPages((prev) => { return {...prev, totalPages: total}})
        }
        const f_func = (problem) => {
            setState((prev) => { return {...prev,
                loading: false,
                problem: problem
            }})
        }
        const func_obj = {success: s_func, failed: f_func}
        return request(url(pages.page), {method: 'GET'}, func_obj)
    }, [pages.page])

    useEffect(() => {
        return loadPage().cancel
    }, [loadPage])

    const onCloseHandler = useCallback(() => setState((prev) => { return {...prev, problem: null}}), [])
    const modalCloseHandler = useCallback(() => setModal(false), [])

    const handleNextPage = useCallback(() => {
        setPages((prev) => {
            let nextPage = prev.page + 1
            if(nextPage >= prev.totalPages) nextPage = 0
            return {...prev, page: nextPage}
        })
    }, [])

    const handlePrevPage = useCallback(() => {
        setPages((prev) => {
            let nextPage = prev.page - 1
            if(nextPage < 0) nextPage = prev.totalPages - 1
            return {...prev, page: nextPage}
        })
    }, [])

    let main_content = null
    let template_list = null
    if(state.loading) main_content = <div className="text-center"><Spinner animation="border" style={{width: "3rem", height: "3rem"}}/></div>
    if(state.data != null) {
        main_content = <Button variant="success" type="button" className="mb-3" onClick={() => setModal(true)}>New Template</Button>
        template_list = state.data.entities.map((elem) => {
            return <TemplateCard key={elem.properties.id} template={elem.properties} reload={loadPage}/>
        })
    }

    let pages_buttons = null
    if(pages.totalPages > 1) pages_buttons = <Row>
        <Col><Button variant="success" className="col-12" onClick={handlePrevPage}>Prev</Button></Col>
        <Col><Button variant="success" className="col-12" onClick={handleNextPage}>Next</Button></Col>
    </Row>

    let modal_content = null
    if(modal) modal_content = <Modal show={true}>
        <NewTemplateModal reload={loadPage} href={uri} onClose={modalCloseHandler}/>
    </Modal>

    return (
        <div className="mb-3">
            <Container>
                <Row><Notification problem={state.problem} onClose={onCloseHandler}/></Row>
                <Row><h1>Templates</h1></Row>
                <Row>{main_content}</Row>
            </Container>
            <Container>
                <Row>{template_list}</Row>
                {pages_buttons}
            </Container>
            {modal_content}
        </div>
    )
}

