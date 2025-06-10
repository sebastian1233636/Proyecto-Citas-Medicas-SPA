import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import './registroExitoso.css';

function RegistroExitoso() {
    const navigate = useNavigate();

    useEffect(() => {
        const timeout = setTimeout(() => {
            navigate("/login");
        }, 5000);

        return () => clearTimeout(timeout);
    }, [navigate]);

    return (
        <div className="registroExitoso-wrapper">

            <div className="registroExitoso-container">
                <h2>Gracias, tu usuario ha sido registrado.</h2>
            </div>
        </div>

    );
}

export default RegistroExitoso;
