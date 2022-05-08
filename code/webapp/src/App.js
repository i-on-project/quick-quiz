import React from "react";
import './App.css';
import {LogMeIn} from "./Components/LogMeIn";
import { UserContextProvider } from "./Components/UserContextProvider";
import {HomeComponent} from "./Components/HomeComponent";
import {RegisterUser} from "./Components/RegisterUser";
import {LoginUser} from "./Components/LoginUser";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import {NavbarComponentAuth} from "./Components/NavBarComponentAuth";

function App() {

    //var stompClient = require('./Utils/websocket-listener')

    return (

            <UserContextProvider>
                <Router>
                    <div>
                        <NavbarComponentAuth/>
                        <Routes>
                            <Route path="/" element={<HomeComponent/>}/>
                            <Route path="/register" element={<RegisterUser/>}/>
                            <Route path="/login" element={<LoginUser/>}/>
                            <Route path="/logmein" element={<LogMeIn/>}/>
                        </Routes>
                    </div>
                </Router>
            </UserContextProvider>

    );
}

export default App;

