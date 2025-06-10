import './registroMedico.css';
import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

export default function RegistroMedico() {
    const { id } = useParams();
    const navigate = useNavigate();
    const backend = "http://localhost:8080";

    const [formData, setFormData] = useState({
        especialidad: "",
        costo: "",
        localidad: "",
        frecuencia: ""
    });

    const [error, setError] = useState("");

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        try {
            const response = await fetch(`${backend}/Medico/register/${id}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    especialidad: formData.especialidad,
                    costo: Number(formData.costo),
                    localidad: formData.localidad,
                    frecuenciaCitas: Number(formData.frecuencia)
                })
            });

            if (response.ok) {
                navigate("/registroExitoso");
            } else {
                const message = await response.text();
                setError("Error al registrar médico: " + message);
            }
        } catch (err) {
            console.error(err);
            setError("Error al conectar con el servidor.");
        }
    };

    return (
        <div className="registerDoc-container">
            <form onSubmit={handleSubmit} className="registerDoc-box">
                <h2>Información del Médico</h2>
                <img src="/user.png" alt="User Icon" />

                <div className="input-group">
                    <i className="fas fa-heartbeat"></i>
                    <input
                        type="text"
                        name="especialidad"
                        placeholder="Especialidad"
                        required
                        pattern="[A-Za-z\s]+"
                        value={formData.especialidad}
                        onChange={handleChange}
                    />
                </div>

                <div className="input-group">
                    <i className="fas fa-coins"></i>
                    <input
                        type="number"
                        name="costo"
                        placeholder="Costo"
                        required
                        min="1"
                        value={formData.costo}
                        onChange={handleChange}
                    />
                </div>

                <div className="input-group">
                    <i className="fas fa-map"></i>
                    <input
                        type="text"
                        name="localidad"
                        placeholder="Localidad"
                        required
                        value={formData.localidad}
                        onChange={handleChange}
                    />
                </div>

                <div className="input-group">
                    <i className="fas fa-clock"></i>
                    <input
                        type="number"
                        name="frecuencia"
                        placeholder="Frecuencia (minutos)"
                        required
                        min="1"
                        value={formData.frecuencia}
                        onChange={handleChange}
                    />
                </div>

                <button type="submit" className="button">Registrar Médico</button>

                {error && <div className="error"><p>{error}</p></div>}
            </form>
        </div>
    );
}
