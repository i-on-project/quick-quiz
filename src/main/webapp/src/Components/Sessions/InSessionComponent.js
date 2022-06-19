import React, {Fragment, useEffect, useState} from "react";
import {Card, Container, Row} from "react-bootstrap";
import {useParams} from "react-router-dom";
import SockJsClient from 'react-stomp';
import {goGET} from "../../Services/FetchService";


import {QuizAnswerCardInSession} from "../Quizzes/QuizAnswerCardInSession";

export const InSession = () => {
    const [answer, setAnswer] = useState(null)
    const [quizzes, setQuizzes] = useState(null)
    const [client, setClient] = useState(null)
    const [wsSourceUrl, setWsSourceUrl] = useState(null)
    const {id} = useParams()



    const getQuizzes = () => {
        const setError = (error) => {
            console.log(`Failed to get session with ID: ${id} with error ${error}`)
        }
        const setQuestionsData = (data) => {
            //const tmp = data.entities.map(e => e.properties)
            setQuizzes(data.entities)
            if(client !== null) {
                console.log(`client: ${client}`)
                console.log(`/topic/insession/${answer.sessionId}`)
                sendMessageToOrganizer()
            }
        }
        console.log(`Get Quizzes AnswerId: ${id}`)
        if(id !== 'iframe.html')
            goGET(`/api/web/v1.0/non_auth/quiz/session/${id}`, setQuestionsData, setError)
    }
    useEffect(() => {
        /*Prevent reset state*/
        const setSessionError = (error) => {
            console.log(`Failed to get session with ID: ${id} with error ${error}`)
        }

        const getMeSession = (data) => {
            setAnswer(data)
            //sendMessageToOrganizer()
            console.log(data)
        }
        console.log(`Get Session AnswerId: ${id}`)
        setWsSourceUrl( "/insessionws") //window.location.protocol + "//" + window.location.host +

        if(id !== 'iframe.html')
            goGET(`/api/web/v1.0/non_auth/answer/${id}`, getMeSession, setSessionError)
    }, [id])

    useEffect(() => {
        if(client !== null) {
            console.log(`client: ${client}`)
            console.log(`/topic/insession/${answer.sessionId}`)
            sendMessageToOrganizer()
        }
        getQuizzes()
    }, [answer])

/*    useEffect(() => {
        if(client !== null) {
            console.log(`client: ${client}`)
            console.log(`/topic/insession/${answer.sessionId}`)
            //sendMessageToOrganizer()
        }
    }, [client])*/

    const getQuizAnswer = (id) => answer.answers.find(a => a.quizId === id)

    const sendMessageToOrganizer = () => {

        client.sendMessage(`/queue/insession/${answer.sessionId}`, JSON.stringify({
            name: 'Participant',
            message: 'Joined Session - New/updated answer'
        }));
    }

    return (

        <Fragment>
            <h1 className={"text-center mb-5 mt-3"}>Participant: {id}</h1>
            <Container>
                <Row>
                    {quizzes !== null && quizzes.length > 0 && answer !== null && (
                        quizzes.map(q => <QuizAnswerCardInSession key={q.properties.id}
                                                                  name={q.properties.question}
                                                                  data={q.properties}
                                                                  quizHref={q.href}
                                                                  reloadQuizzes={getQuizzes}
                                                                  answer={getQuizAnswer(q.properties.id)}
                                                                  answerId={id}
                                                                  messageOrganizer={sendMessageToOrganizer}
                            //sendTestMessage={sendTestMessage}
                        />)
                    )}
                </Row>
            </Container>


            {answer !== null && wsSourceUrl !== null && <SockJsClient url= {`${wsSourceUrl}`}
                                              topics={[`/topic/insession/${answer.sessionId}`]}
                                              onConnect={() => {
                                                  console.log("connected");
                                              }}
                                              onDisconnect={() => {
                                                  console.log("Disconnected");
                                              }}
                                              onMessage={(msg) => {
                                                  {/*need to add validation here*/}
                                                  console.log(`gotta message`)
                                                  console.log(msg)
                                                  if(msg.name === 'Organizer')
                                                    getQuizzes()
                                              }}
                                              ref={(client) => setClient(client)
                                              } />
            }
        </Fragment>

    );
}

