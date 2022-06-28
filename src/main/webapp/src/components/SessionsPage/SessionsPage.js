import * as React from "react"
import {useCallback, useEffect, useState} from "react";
import {Button, Col, Container, Modal, Row, Spinner} from "react-bootstrap";
import {Notification} from "../Notification";
import {NewSessionModal} from "./NewSessionModal";
import {SessionCard} from "./SessionCard";
import {request} from "../../utils/Request";
import {getActionHref, getLinksFromEntity} from "../../utils/SirenJson";

const page_size = 10
const uri = '/api/web/v1.0/auth/sessions?page='
export const Sessions = () => {

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
        return request(`${uri}${pages.page}`, {method: 'GET'}, func_obj).cancel
    }, [pages.page])

    useEffect(() => {
        return loadPage()
    }, [loadPage])

    const onCloseHandler = useCallback(() => {
        setState((prev) => { return {...prev, problem: null}})
    }, [])

    const modalCloseHandler = useCallback(() => {
        setModal(false)
    }, [])

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
    let session_list = null
    if(state.loading) main_content = <div className="text-center"><Spinner animation="border" style={{width: "3rem", height: "3rem"}}/></div>
    if(state.data != null) {
        main_content = <Button variant="success" type="button" className="mb-3" onClick={() => setModal(true)}>New Session</Button>
        session_list = state.data.entities.map((elem) => {
            const links_obj = {
                delete_href: getLinksFromEntity(elem, 'delete'),
                start_href: getLinksFromEntity(elem, 'start'),
                close_href: getLinksFromEntity(elem, 'close')
            }
            return <SessionCard key={elem.properties.id} session={elem} links={links_obj} reload={loadPage}/>
        })
    }

    let pages_buttons = null
    if(pages.totalPages > 1) pages_buttons = <Row>
        <Col><Button variant="success" className="col-12" onClick={handlePrevPage}>Prev</Button></Col>
        <Col><Button variant="success" className="col-12" onClick={handleNextPage}>Next</Button></Col>
    </Row>

    let modal_content = null
    if(modal) modal_content = <Modal show={true}>
        <NewSessionModal reload={loadPage} href={getActionHref(state.data.actions, "Create-Session")} onClose={modalCloseHandler}/>
    </Modal>

    return (
        <div className="mb-3">
            <Container>
                <Row><Notification problem={state.problem} onClose={onCloseHandler}/></Row>
                <Row><h1>Sessions</h1></Row>
                <Row>{main_content}</Row>
            </Container>
            <Container>
                <Row>{session_list}</Row>
                {pages_buttons}
            </Container>
            {modal_content}
        </div>
    )
}

