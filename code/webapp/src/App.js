import React, {useContext, useEffect} from "react";
import './App.css';
import {LogMeIn} from "./Components/LogMeIn";
import {UserContext, UserContextProvider} from "./Components/UserContextProvider";
import {HomeComponent} from "./Components/HomeComponent";
import {RegisterUser} from "./Components/RegisterUser";
import {LoginUser} from "./Components/LoginUser";
import {BrowserRouter as Router, Navigate, Route, Routes} from "react-router-dom";
import {NavBarComponent} from "./Components/NavBarComponent";
import {Templates} from "./Components/Templates/Templates";
import {Sessions} from "./Components/Sessions/Sessions";
import {Session} from "./Components/Sessions/Session";
import History from "./Components/History/HistoryPage";
import {InSession} from "./Components/Sessions/InSessionComponent";
import {InSessionOrg} from "./Components/Sessions/InSessionOrg";



function App() {

    //var stompClient = require('./Utils/websocket-listener')
    const userContext = useContext(UserContext)

   // const isLoggedIn = () => userContext.userName !== null && userContext.isLoading === false

    return (
            <UserContextProvider>
                <Router>
                    <div>
                        <NavBarComponent />
                        <Routes>
                            {/**No auth needed**/}
                            <Route path="/" element={<HomeComponent/>}/>
                            <Route path="/register" element={<RegisterUser/>}/>
                            <Route path="/login" element={<LoginUser/>}/>
                            <Route path="/logmein" element={<LogMeIn/>}/>
                            <Route path="/insession/:id" element={<InSession/>}/>
                             {/**auth needed**/}
                            <Route path="/sessions" element={<Sessions/>}/>
                            <Route path="/sessions/:id" element={<Session/>}/>
                            <Route path="/owninsession/:id" element={<InSessionOrg/>}/>
                            <Route path="/templates" element={<Templates/>}/>
                            <Route path="/history" element={<History />} />

                        </Routes>
                    </div>
                </Router>
            </UserContextProvider>
    );
}

export default App;

