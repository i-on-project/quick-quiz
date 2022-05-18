import React, {useContext, useEffect} from "react";
import './App.css';
import {LogMeIn} from "./Components/LogMeIn";
import {UserContext, UserContextProvider} from "./Components/UserContextProvider";
import {HomeComponent} from "./Components/HomeComponent";
import {RegisterUser} from "./Components/RegisterUser";
import {LoginUser} from "./Components/LoginUser";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import {NavBarComponent} from "./Components/NavBarComponent";
import {Templates} from "./Components/Templates/Templates";
import {Sessions} from "./Components/Sessions/Sessions";
import {Session} from "./Components/Sessions/Session";
import History from "./Components/History/HistoryPage";


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
                            <Route path="/" element={<HomeComponent/>}/>
                            <Route path="/register" element={<RegisterUser/>}/>
                            <Route path="/sessions" element={<Sessions/>}/>
                            <Route path="/sessions/:id" element={<Session/>}/>
                           {/* <Route path="/insession/:id" element={<Session/>}/>*/}
                            <Route path="/templates" element={<Templates/>}/>
                            <Route path="/history" element={<History />} />
                            <Route path="/login" element={<LoginUser/>}/>
                            <Route path="/logmein" element={<LogMeIn/>}/>
                        </Routes>
                    </div>
                </Router>
            </UserContextProvider>
    );
}

export default App;

