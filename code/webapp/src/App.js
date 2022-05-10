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


function App() {

    //var stompClient = require('./Utils/websocket-listener')
    const userContext = useContext(UserContext)
    useEffect(() => {
        console.log(`Is LoggedIn: ${userContext.isLoggedIn}`)
    }, [])
    return (
            <UserContextProvider>
                <Router>
                    <div>
                        <NavBarComponent />
                        <Routes>
                            <Route path="/" element={<HomeComponent/>}/>
                            <Route path="/register" element={<RegisterUser/>}/>
                            <Route path="/sessions" element={<Sessions/>}/>
                            <Route path="/templates" element={<Templates/>}/>
                            <Route path="/login" element={<LoginUser/>}/>
                            <Route path="/logmein" element={<LogMeIn/>}/>
                        </Routes>
                    </div>
                </Router>
            </UserContextProvider>
    );
}

export default App;

