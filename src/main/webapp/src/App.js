import * as React from "react"
import {BrowserRouter, Routes, Route} from "react-router-dom";
import {NavigationBar} from "./components/Navbar";
import {UserProvider} from "./components/UserContext";
import {ParticipantProvider} from "./components/ParticipantContext";
import {Home} from "./components/HomePage/Home";
import {Sessions} from "./components/SessionsPage/SessionsPage";
import {Session} from "./components/SessionsPage/SessionPage";
import {LiveSession} from "./components/SessionsPage/LiveSessionPage";
import {ParticipantPage} from "./components/ParticipantPage/ParticipantPage";
import {Login} from "./components/LoginPage";
import {Register} from "./components/RegisterPage";
import {LogMeIn} from "./components/LogmeinPage";
import {LoginVerifier} from "./components/LoginVerifier";
import {PageNotFound} from "./components/PageNotFound";
import {History} from "./components/HistoryPage/HistoryPage";
import {Templates} from "./components/TemplatesPage/TemplatesPage";
import {Template} from "./components/TemplatesPage/TemplatePage";

function App() {
  return(
      <BrowserRouter>
          <UserProvider>
              <NavigationBar />
              <ParticipantProvider>
                <Routes>
                    <Route path="/" element={<Home/>} />
                    <Route path="/logmein" element={<LogMeIn/>}/>
                    <Route path="/login" element={<Login/>}/>
                    <Route path="/register" element={<Register/>}/>
                    <Route path="/insession/:id" element={<ParticipantPage/>}/>

                    <Route path="/sessions" element={<LoginVerifier><Sessions/></LoginVerifier>}/>
                    <Route path="/session/:id" element={<LoginVerifier><Session/></LoginVerifier>}/>
                    <Route path="/live_session/:id" element={<LoginVerifier><LiveSession/></LoginVerifier>}/>
                    <Route path="/history" element={<LoginVerifier><History/></LoginVerifier>}/>
                    <Route path="/templates" element={<LoginVerifier><Templates/></LoginVerifier>}/>
                    <Route path="/template/:id" element={<LoginVerifier><Template/></LoginVerifier>}/>

                    <Route path="*" element={<PageNotFound/>}/>
                </Routes>
              </ParticipantProvider>
          </UserProvider>
      </BrowserRouter>
  )
}

export default App;
