import {NavBarComponent} from "./NavBarComponent";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import {HomeComponent} from "./HomeComponent";
import {RegisterUser} from "./RegisterUser";
import {LoginUser} from "./LoginUser";
import {LogMeIn} from "./LogMeIn";
import React from "react";

export const MasterComponent = () => {
    let user = null
    return (
        <div>
            <Router>
                <div>
                    <NavBarComponent/>
                    <Routes>
                        <Route path="/" element={<HomeComponent/>}/>
                        <Route path="/register" element={<RegisterUser/>}/>
                        <Route path="/login" element={<LoginUser/>}/>
                        <Route path="/logmein" element={<LogMeIn/>}/>
                    </Routes>
                </div>
            </Router>
        </div>
    )
}