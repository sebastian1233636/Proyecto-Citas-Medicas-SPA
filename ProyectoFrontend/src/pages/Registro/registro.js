import './registro.css';
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Registro() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        id: "",
        clave: "",
        nombre: "",
        rolId: "",
        imagen: null
    });

    const [confirmClave, setConfirmClave] = useState(""); // Nuevo estado para confirmar la contraseña
    const [error, setError] = useState("");

    const backend = "http://localhost:8080";

    const handleChange = (e) => {
        const { name, value, files } = e.target;

        if (name === "confirmClave") {
            setConfirmClave(value);
        } else {
            setFormData({
                ...formData,
                [name]: files ? files[0] : value
            });
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        // Validar que las contraseñas coincidan
        if (formData.clave !== confirmClave) {
            setError("Las contraseñas no coinciden.");
            return;
        }

        try {
            const dataToSend = new FormData();
            for (let key in formData) {
                dataToSend.append(key, formData[key]);
            }

            const response = await fetch(`${backend}/user/register`, {
                method: "POST",
                body: dataToSend,
            });

            if (response.redirected) {
                window.location.href = response.url;
                return;
            }

            if (response.ok) {
                const rolId = formData.rolId;
                const id = formData.id;

                if (rolId === "2") {
                    navigate(`/registro-medico/${id}`);
                } else {
                    navigate("/login");
                }
            } else {
                const errorMsg = await response.text();
                setError("Error al registrar: " + errorMsg);
            }
        } catch (err) {
            console.error("Error:", err);
            setError("Error al conectar con el servidor.");
        }
    };

    return (
        <div className="register-container">
            <form onSubmit={handleSubmit} className="register-box" encType="multipart/form-data">
                <h2>Registro</h2>
                <img src="/user.png" alt="User Icon" />

                <div className="input-group">
                    <i className="fas fa-user"></i>
                    <input type="text" name="id" placeholder="User ID" required value={formData.id} onChange={handleChange} />
                </div>

                <div className="input-group">
                    <i className="fas fa-key"></i>
                    <input type="password" name="clave" placeholder="Contraseña" required value={formData.clave} onChange={handleChange} />
                </div>

                <div className="input-group">
                    <i className="fas fa-key"></i>
                    <input type="password" name="confirmClave" placeholder="Confirmar Contraseña" required value={confirmClave} onChange={handleChange} />
                </div>

                <div className="input-group">
                    <i className="fas fa-id-card"></i>
                    <input type="text" name="nombre" placeholder="Nombre" required pattern="[A-Za-z\s]+" value={formData.nombre} onChange={handleChange} />
                </div>

                <div className="input-group">
                    <i className="fas fa-user-tag"></i>
                    <select name="rolId" value={formData.rolId} onChange={handleChange} required>
                        <option value="">Seleccione un rol</option>
                        <option value="1">Paciente</option>
                        <option value="2">Médico</option>
                    </select>
                </div>

                <div className="input-group">
                    <i className="fas fa-camera"></i>
                    <input type="file" name="imagen" accept="image/jpeg,image/png" onChange={handleChange} />
                </div>

                <button type="submit" className="button">Registrarse</button>

                {error && <div className="error"><p>{error}</p></div>}
            </form>
        </div>
    );
}