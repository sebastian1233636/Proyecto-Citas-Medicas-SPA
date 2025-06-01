import React, { createContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";

const AppContext = createContext();

function AppProvider(props) {
    const [authState, setAuthState] = useState({
        token: null,
        user: null,
    });

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            try {
                const decoded = jwtDecode(token);
                setAuthState({ token, user: decoded });
            } catch (e) {
                console.error("Token inválido:", e);
                localStorage.removeItem("token");
            }
        }
    }, []);

    const login = (token) => {
        try {
            const decoded = jwtDecode(token);
            localStorage.setItem("token", token);
            setAuthState({ token, user: decoded });
        } catch (e) {
            console.error("Error al decodificar el token:", e);
        }
    };

    return (
        <AppContext.Provider value={{ authState, setAuthState, login }}>
            {props.children}
        </AppContext.Provider>
    );
}

export { AppProvider, AppContext };