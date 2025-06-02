import './App.css';
import About from './pages/About/about';
import Login from './pages/Login/login';
import Logout from './pages/Login/logout';
import Home from './pages/Home/home';
import { useContext } from "react";
import { AppContext } from "./AppProvider";
import { AppProvider } from "./AppProvider";
import { Link, BrowserRouter, Routes, Route } from 'react-router-dom';

function App() {
    return (
        <AppProvider>
            <BrowserRouter>
                <div className="App">
                    <Header />
                    <Main />
                    <Footer />
                </div>
            </BrowserRouter>
        </AppProvider>
    );
}

function Header() {
    const { authState } = useContext(AppContext);
    const rol = authState.user?.rol;

    console.log("Estado de autenticación:", authState);

    return (
        <header className="App-header">
            <div className="App-header-title">
                <img src="/logo.jpg" alt="logo" className="App-logo" />
                <p>Medical Appointments</p>
            </div>
            <nav className="App-nav">
                {!rol && (
                    <>
                        <Link to="/home" className="App-link">Search</Link>
                        <Link to="/login" className="App-link">Login</Link>
                    </>
                )}
                {rol === 1 && (
                    <>
                        <Link to="/historialPaciente" className="App-link">Historial</Link>
                        <Link to="/home" className="App-link">Search</Link>
                        <Link to="/perfil" className="App-link">Mi Perfil</Link>
                        <Link to="/logout" className="App-link">Logout</Link>
                    </>
                )}
                {rol === 2 && (
                    <>
                        <Link to="/historial" className="App-link">Citas</Link>
                        <Link to="/home" className="App-link">Search</Link>
                        <Link to="/perfil" className="App-link">Mi Perfil</Link>
                        <Link to="/logout" className="App-link">Logout</Link>
                    </>
                )}
                {rol === 3 && (
                    <>
                        <Link to="/gestion" className="App-link">Gestión</Link>
                        <Link to="/home" className="App-link">Search</Link>
                        <Link to="/logout" className="App-link">Logout</Link>
                    </>
                )}
            </nav>
        </header>
    );
}

function Main() {
    return (
        <div className="App-Main">
            <Routes>
                <Route path="/about" element={<About />} />
                <Route path="/login" element={<Login />} />
                <Route path="/logout" element={<Logout />} />
                <Route path="/home" element={<Home />} />
            </Routes>
        </div>
    );
}

function Footer() {
    return (
        <div className="App-footer">
            <div>Total Soft Inc.</div>
            <div className="social-icons">
                <img src="/twitter.png" alt="Twitter"/>
                <img src="/facebook.png" alt="Facebook"/>
                <img src="/instagram.png" alt="Instagram"/>
            </div>
            <div>©2019 Tsf, Inc.</div>
        </div>
    );
}

export default App;
