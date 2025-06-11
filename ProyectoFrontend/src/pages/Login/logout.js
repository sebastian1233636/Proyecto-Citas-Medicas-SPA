import { AppContext } from "../../AppProvider";
import { useNavigate } from "react-router-dom";
import { useEffect, useContext } from "react";

export default function Logout() {
    const { setAuthState } = useContext(AppContext);
    const navigate = useNavigate();

    useEffect(() => {
        setAuthState({ isAuthenticated: false, user: null, token: null });

        localStorage.removeItem("token");
        navigate("/login");
    }, [setAuthState, navigate]);

    return null;
}