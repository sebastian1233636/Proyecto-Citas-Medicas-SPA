import './login.css';
import React, { useState, useContext } from "react";
import { AppContext } from '../../AppProvider';
import { useNavigate } from "react-router-dom";

export default function Login() {
    const { login } = useContext(AppContext);
    const [id, setid] = useState("");
    const [clave, setclave] = useState("");
    const [error, setError] = useState(false);
    const navigate = useNavigate();

    var backend = "http://localhost:8080";

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(false);

        try {
            const response = await fetch( backend + "/user/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ id: id, clave: clave }),
            });
            if (response.ok) {
                const data = await response.json();
                login(data.token);
                navigate("/home");
            } else {
                setError(true);
            }
        } catch (err) {
            console.error("Error al intentar loguear:", err);
            setError(true);
        }
    };

    return (
        <div className="login-container">
            <form onSubmit={handleSubmit} className="login-box">
                <h2>Login</h2>
                <img src="/user.png" alt="User Icon" />

                <div className="input-group">
                    <i className="fas fa-user"></i>
                    <input
                        type="text"
                        placeholder="Usuario"
                        value={id}
                        onChange={(e) => setid(e.target.value)}
                        required
                    />
                </div>

                <div className="input-group">
                    <i className="fas fa-key"></i>
                    <input
                        type="password"
                        placeholder="Clave"
                        value={clave}
                        onChange={(e) => setclave(e.target.value)}
                        required
                    />
                </div>

                <div className="login-btn">
                    <button type="submit" className="button">
                        Login
                    </button>
                </div>

                <div className="register">
                    <p>Don't have an account?</p>
                    <a href="/registro" className="register-btn">
                        Register here
                    </a>
                </div>

                {error && (
                    <div className="error">
                        <p>Credenciales Incorrectas</p>
                    </div>
                )}
            </form>
        </div>
    );
}
