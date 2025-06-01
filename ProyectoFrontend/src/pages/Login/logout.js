import { useEffect, useContext } from "react";
import { AppContext } from "../../AppProvider";
import { useNavigate } from "react-router-dom";

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
