import * as React from "react";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {UserProvider} from "./components/UserContext";
import {NavigationBar} from "./components/Navbar";
import {Home} from "./components/HomePage/Home";
import {LogMeIn} from "./components/LogmeinPage";
import {Login} from "./components/LoginPage";
import {Register} from "./components/RegisterPage";
import {ParticipantPage} from "./components/ParticipantPage/ParticipantPage";
import {LoginVerifier} from "./components/LoginVerifier";
import {Sessions} from "./components/SessionsPage/SessionsPage";
import {Session} from "./components/SessionsPage/SessionPage";
import {LiveSession} from "./components/SessionsPage/LiveSessionPage";
import {History} from "./components/HistoryPage/HistoryPage";
import {Templates} from "./components/TemplatesPage/TemplatesPage";
import {Template} from "./components/TemplatesPage/TemplatePage";
import {PageNotFound} from "./components/PageNotFound";

const App = () => {
    return (
        <BrowserRouter>
            <UserProvider>
                <NavigationBar/>
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
            </UserProvider>
        </BrowserRouter>
    )
}

export default App