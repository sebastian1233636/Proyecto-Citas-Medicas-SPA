import AvatarConFallback from "../MiPerfil/AvatarConFallback";
import React, { useState, useEffect } from "react";
import './gestion.css';

const GestionMedicos = () => {
    const [medicos, setMedicos] = useState([]);
    const fetchMedicos = async () => {
        try {
            const token = localStorage.getItem("token");
            const url = "http://localhost:8080/Medico/gestion";

            const headers = {
                "Content-Type": "application/json"
            };

            if (token) {
                headers["Authorization"] = `Bearer ${token}`;
            }

            const response = await fetch(url, {
                method: "GET",
                headers: headers
            });


            if (!response.ok) {
                throw new Error("Error al obtener médicos");
            }

            const data = await response.json();
            console.log("Datos médicos del backend:", data);
            setMedicos(data);


        } catch (error) {
            console.error("Error al obtener médicos:", error);
        }
    };

    const handleAceptar = async (id) => {
        try {
            const token = localStorage.getItem("token");
            const url = `http://localhost:8080/Medico/gestion/${id}`;

            const headers = {
                "Content-Type": "application/json"
            };

            if (token) {
                headers["Authorization"] = `Bearer ${token}`;
            }

            const response = await fetch(url, {
                method: "PUT",
                headers: headers
            });

            if (response.ok) {
                setMedicos(prev => prev.filter(m => m.id !== id));
            } else {
                throw new Error("Error al aceptar médico");
            }
        } catch (error) {
            console.error("Error al aceptar médico:", error);
        }
    };

    useEffect(() => {
        fetchMedicos();
    }, []);

    return (
        <div>
            <h2 className="gestion-title">Médicos Pendientes de Aprobación</h2>
            <table className="gestion-table">
                <thead>
                <tr>
                    <th>Foto</th>
                    <th>Nombre</th>
                    <th>Especialidad</th>
                    <th>Localidad</th>
                    <th>Costo</th>
                    <th>Acción</th>
                </tr>
                </thead>
                <tbody>
                {medicos
                    .filter(medico => medico.status === 'Pendiente')
                    .map(medico => (
                        <tr key={medico.id} className="medicos-row">
                            <td>
                                <AvatarConFallback
                                    src={`http://localhost:8080/user/imagen/${medico.id}`}
                                    fallbackText={medico.nombre || 'U'}
                                    className="user-avatar"
                                />
                            </td>
                            <td>{medico.nombre}</td>
                            <td>{medico.especialidad}</td>
                            <td>{medico.localidad}</td>
                            <td>{medico.costo}</td>
                            <td>
                                <button
                                    type="button"
                                    className="btn-accept"
                                    onClick={() => handleAceptar(medico.id)}
                                >
                                    Aceptar
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

        </div>
    );
};

export default GestionMedicos;
