import React, { useState, useEffect, useContext } from "react";
import { AppContext } from '../../AppProvider';
import { useNavigate } from "react-router-dom";
import './home.css';

function Home() {
    const backend = "http://localhost:8080";
    const [medicos, setMedicos] = useState([]);
    const [disponibilidad, setDisponibilidad] = useState({});
    const [selectedDoctor, setSelectedDoctor] = useState(null);
    const [selectedDate, setSelectedDate] = useState("");
    const [selectedTime, setSelectedTime] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [especialidad, setEspecialidad] = useState("");
    const [localidad, setLocalidad] = useState("");

    // Nueva variable para guardar citas confirmadas
    const [confirmedAppointments, setConfirmedAppointments] = useState([]);

    const { user } = useContext(AppContext);
    const navigate = useNavigate();

    useEffect(() => {
        fetchMedicos();
    }, []);

    const handleTimeClick = (doctor, date, time) => {
        setSelectedDoctor(doctor);
        setSelectedDate(date);
        setSelectedTime(time);
        setShowModal(true);
    };

    const handleConfirm = async () => {
        try {
            const token = localStorage.getItem("token");
            const doctorId = selectedDoctor.id;
            const dateTime = `${selectedDate}T${selectedTime}`;

            const response = await fetch(`${backend}/citas/appointment/confirm?did=${doctorId}&ddt=${dateTime}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                alert("¡Cita confirmada con éxito!");
                setConfirmedAppointments(prev => [...prev, { doctorId, date: selectedDate, time: selectedTime }]);
                setShowModal(false);
            } else {
                alert("Error al confirmar la cita");
            }
        } catch (err) {
            console.error("Error al confirmar la cita:", err);
            alert("Ocurrió un error al confirmar la cita");
        }
    };

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
                                                {new Date(fecha).toLocaleDateString("es-ES")}
                                            </div>
                                            <div className="times">
                                                {horas.map((hora, j) => {
                                                    const isConfirmed = confirmedAppointments.some(app =>
                                                        app.doctorId === medico.id &&
                                                        app.date === fecha &&
                                                        app.time === hora
                                                    );

                                                    return (
                                                        <button
                                                            key={j}
                                                            onClick={() => handleTimeClick(medico, fecha, hora)}
                                                            disabled={isConfirmed}
                                                            className={isConfirmed ? 'disabled-time' : ''}
                                                        >
                                                            {hora}
                                                        </button>
                                                    );
                                                })}
                                            </div>
                                        </div>
                                    ))}
                            </div>
                        </td>

                        <td className="button-container">
                            <button
                                className="btn btn-primary"
                                onClick={() => navigate(`/home/${medico.id}/schedule`)}
                            >
                                Schedule
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {showModal && selectedDoctor && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h2>Confirm Appointment</h2>
                        <p><strong>Doctor:</strong> {selectedDoctor.nombre}</p>
                        <p><strong>Specialty:</strong> {selectedDoctor.especialidad}</p>
                        <p><strong>City:</strong> {selectedDoctor.localidad}</p>
                        <p><strong>Date:</strong> {new Date(selectedDate).toLocaleDateString("es-ES")}</p>
                        <p><strong>Time:</strong> {selectedTime}</p>
                        <div className="modal-buttons">
                            <button
                                className="btn btn-success"
                                onClick={handleConfirm}
                            >
                                Confirm
                            </button>
                            <button
                                className="btn btn-secondary"
                                onClick={() => setShowModal(false)}
                            >
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default Home;
