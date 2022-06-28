import * as React from "react"
import {BrowserRouter, Routes, Route} from "react-router-dom";
import {NavigationBar} from "./components/Navbar";
import {UserProvider} from "./components/UserContext";
import {ParticipantProvider} from "./components/ParticipantContext";
import {Home} from "./components/HomePage/Home";
import {Sessions} from "./components/SessionsPage/SessionsPage";
import {Session} from "./components/SessionsPage/SessionPage";
import {LiveSession} from "./components/SessionsPage/LiveSessionPage";

function App() {
  return(
      <BrowserRouter>
          <UserProvider>
              <NavigationBar />
              <ParticipantProvider>
                <Routes>
                    <Route path="/" element={<Home/>} />
                    <Route path="/sessions" element={<Sessions/>} />
                    <Route path="/session/:id" element={<Session/>} />
                    <Route path="/live_session/:id" element={<LiveSession/>} />
                </Routes>
              </ParticipantProvider>
          </UserProvider>
      </BrowserRouter>
  )
}

export default App;
