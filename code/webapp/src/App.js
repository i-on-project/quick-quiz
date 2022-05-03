import React, {useState} from "react";
//import {BrowserRouter, Route} from 'react-router-dom'
import {
  BrowserRouter as Router,
  Routes,
  Route,
  useRouteMatch,
  useParams
} from "react-router-dom";



import './App.css';
import {RegisterUser} from "./Components/RegisterUser";
import {NavbarComponent} from "./Components/NavbarComponent";
import {HomeComponent} from "./Components/HomeComponent";
import {InSession} from "./Components/InSessionComponent";


function App() {
  return (
      <Router>
        <div>
          <NavbarComponent/>
          <Routes>
            <Route path="/" element={<HomeComponent/>}/>
            <Route path="/register" element={<RegisterUser/>}/>
            <Route path="/insession" element={<InSession />} />
          </Routes>
        </div>
      </Router>
  );
}

export default App;

