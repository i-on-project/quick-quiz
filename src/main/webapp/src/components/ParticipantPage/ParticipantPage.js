import * as React from "react";
import {Fragment, useState} from "react";
import {Container, Row} from "react-bootstrap";
import SockJsClient from 'react-stomp';

export const ParticipantPage = () => {

    const [state, setState] = useState({data: null, loading: false, problem: null})


    return(
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
    )
}