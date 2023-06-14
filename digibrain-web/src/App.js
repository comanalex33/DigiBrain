import './App.css';
import {Route, Routes, BrowserRouter} from "react-router-dom";

import Navbar from './navbar/Navbar';
import Home from './pages/Home';
import Login from './pages/Login';
import Learn from './pages/Learn';
import Quiz from './pages/Quiz';
import LearnPath from './pages/LearnPath';
import Admin from './pages/Requests';
import Messages from './pages/Messages';

function App() {
  return (
    <BrowserRouter>
        <Navbar/>
        <Routes>
            <Route path="" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/learn" element={<Learn />} />
            <Route path="/quiz" element={<Quiz />} />
            <Route path="/learn-path" element={<LearnPath />}/>
            <Route path="/requests" element={<Admin />}/>
            <Route path="/messages" element={<Messages />}/>
        </Routes>
    </BrowserRouter>
  );
}

export default App;
