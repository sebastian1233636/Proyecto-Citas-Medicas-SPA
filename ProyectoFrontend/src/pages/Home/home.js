import React, { useState, useEffect, useContext } from "react";
import { AppContext } from '../../AppProvider';
import { useNavigate } from "react-router-dom";
import './home.css';

function Home() {
    const backend = "http://localhost:8080";
    const [medicos, setMedicos] = useState([]);
    const [disponibilidad, setDisponibilidad] = useState({});
    const [especialidad, setEspecialidad] = useState("");
    const [localidad, setLocalidad] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        fetchMedicos();
    }, []);

    const fetchMedicos = async (esp = "", loc = "") => {
        try {
            const token = localStorage.getItem("token");
            let url = `${backend}/Medico/home`;

            if (esp || loc) {
                const params = new URLSearchParams();
                if (esp) params.append("especialidad", esp);
                if (loc) params.append("localidad", loc);
                url = `${backend}/Medico/home/filtrado?${params.toString()}`;
            }

            const headers = {
                "Content-Type": "application/json",
            };

            if (token) {
                headers["Authorization"] = `Bearer ${token}`;
            }

            const response = await fetch(url, {
                method: "GET",
                headers: headers
            });

            if (response.ok) {
                const data = await response.json();
                setMedicos(data.medicos);
                setDisponibilidad(data.disponibilidad);
            } else {
                console.error("No autorizado o error del servidor");
            }
        } catch (err) {
            console.error("Error al obtener los médicos:", err);
        }
    };

    const handleEspecialidadChange = (e) => {
        const nuevoValor = e.target.value;
        setEspecialidad(nuevoValor);
        fetchMedicos(nuevoValor, localidad);
    };

    const handleLocalidadChange = (e) => {
        const nuevoValor = e.target.value;
        setLocalidad(nuevoValor);
        fetchMedicos(especialidad, nuevoValor);
    };

    return (
        <>
            <div className="search-bar">
                <form onSubmit={(e) => e.preventDefault()}>
                    <div className="input-container">
                        <div className="input-group">
                            <span className="input-label">Speciality</span>
                            <input
                                type="text"
                                id="especialidad"
                                name="especialidad"
                                value={especialidad}
                                onChange={handleEspecialidadChange}
                            />
                        </div>
                        <div className="input-group">
                            <span className="input-label">City</span>
                            <input
                                type="text"
                                id="localidad"
                                name="localidad"
                                value={localidad}
                                onChange={handleLocalidadChange}
                            />
                        </div>
                        <button type="button" onClick={() => fetchMedicos(especialidad, localidad)}>
                            Search
                        </button>
                    </div>
                </form>
            </div>

            <table className="appointment-table">
                <tbody>
                {medicos.map((medico, index) => (
                    <tr key={index} className="appointment-row">
                        <td className="doctor-info">
                            <img
                                src={`${backend}/user/imagen/${medico.id}`}
                                alt="Medico"
                                className="picture"
                            />
                            <div className="doctor-details">
                                <div className="name-price">
                                    <strong>{medico.nombre}</strong>
                                    <span className="price">{medico.costo}</span>
                                </div>
                                <p>{medico.especialidad}</p>
                                <p className="hospital">{medico.localidad}</p>
                            </div>
                        </td>

                        <td className="availability">
                            <div className="dates-times-container">
                                {disponibilidad[String(medico.id)] &&
                                    Object.entries(disponibilidad[String(medico.id)]).map(([fecha, horas], i) => (
                                        <div key={i}>
                                            <div className="date">
                                                {new Date(fecha + "T00:00:00-06:00").toLocaleDateString("es-ES")}
                                            </div>
                                            <div className="times">
                                                {horas.map((hora, j) => (
                                                    <button
                                                        key={j}
                                                        type="button"
                                                        className="no-action-time"
                                                    >
                                                        {hora}
                                                    </button>
                                                ))}
                                            </div>
                                        </div>
                                    ))}
                            </div>
                        </td>


                        <td className="button-container">
                            <button
                                className="btn btn-primary"
                                onClick={() => {
                                    const token = localStorage.getItem("token");
                                    if (!token) {
                                        navigate("/login");
                                    } else {
                                        navigate(`/home/${medico.id}/schedule`);
                                    }
                                }}
                            >
                                Schedule
                            </button>

                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </>
    );
}

export default Home;
