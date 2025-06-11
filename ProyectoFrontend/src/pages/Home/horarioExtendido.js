import AvatarConFallback from "../MiPerfil/AvatarConFallback";
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './home.css';

function HorarioExtendido() {
    const { id } = useParams();
    const backend = "http://localhost:8080";

    const [medico, setMedico] = useState(null);
    const [disponibilidad, setDisponibilidad] = useState({});
    const [selectedDate, setSelectedDate] = useState("");
    const [selectedTime, setSelectedTime] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [semanaOffset, setSemanaOffset] = useState(0);
    const [confirmedAppointments, setConfirmedAppointments] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem("token");

        const fetchData = async () => {
            try {
                const disponibilidadRes = await fetch(`${backend}/Medico/${id}/schedule?semana=${semanaOffset}`, {
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                });

                if (!disponibilidadRes.ok) throw new Error("Error al cargar disponibilidad");
                const data = await disponibilidadRes.json();

                setDisponibilidad({ [data.id]: data.disponibilidad });
                setMedico({
                    id: data.id,
                    nombre: data.nombre,
                    especialidad: data.especialidad,
                    localidad: data.localidad,
                    costo: data.costo
                });

                const citasRes = await fetch(`${backend}/Historial/medico/${id}`, {
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                });

                if (!citasRes.ok) throw new Error("Error al cargar citas");
                const citas = await citasRes.json();

                const startOfWeek = new Date();
                startOfWeek.setDate(startOfWeek.getDate() + semanaOffset * 7);
                startOfWeek.setHours(0, 0, 0, 0);

                const endOfWeek = new Date(startOfWeek);
                endOfWeek.setDate(endOfWeek.getDate() + 6);
                endOfWeek.setHours(23, 59, 59, 999);

                const citasMapeadas = citas.filter(c => {
                    const citaDate = new Date(c.fecha);
                    return citaDate >= startOfWeek && citaDate <= endOfWeek;
                }).map(c => ({
                    doctorId: id,
                    date: c.fecha,
                    time: c.hora
                }));

                setConfirmedAppointments(citasMapeadas);
            } catch (err) {
                console.error(err);
            }
        };

        fetchData();
    }, [id, semanaOffset]);

    const handleTimeClick = (date, time) => {
        setSelectedDate(date);
        setSelectedTime(time);
        setShowModal(true);
    };

    const handleConfirm = async () => {
        try {
            const token = localStorage.getItem("token");
            const dateTime = `${selectedDate}T${selectedTime}`;

            const response = await fetch(`${backend}/citas/appointment/confirm?did=${id}&ddt=${dateTime}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                alert("¡Cita confirmada con éxito!");
                setConfirmedAppointments(prev => [...prev, { doctorId: id, date: selectedDate, time: selectedTime }]);
                setShowModal(false);
            } else {
                alert("Error al confirmar la cita");
            }
        } catch (err) {
            console.error(err);
            alert("Ocurrió un error al confirmar la cita");
        }
    };

    return (
        <div>
            <table className="appointment-table">
                <tbody>
                <tr className="appointment-row">
                    <td className="button-container">
                        <button
                            onClick={() => setSemanaOffset(prev => Math.max(prev - 1, -1))}
                            className="btn btn-primary"
                            disabled={semanaOffset <= -1}
                        >
                            ← Prev
                        </button>
                    </td>

                    <td className="doctor-info">
                        <AvatarConFallback
                            src={`http://localhost:8080/user/imagen/${id}`}
                            fallbackText={medico?.nombre || 'U'}
                            className="user-avatar"
                        />
                        <div className="doctor-details">
                            <div className="name-price">
                                <strong>{medico?.nombre}</strong>
                                <span className="price">{medico?.costo}</span>
                            </div>
                            <p>{medico?.especialidad}</p>
                            <p className="hospital">{medico?.localidad}</p>
                        </div>
                    </td>

                    <td className="availability">
                        <div className="dates-times-container">
                            {disponibilidad[String(medico?.id)] &&
                                Object.entries(disponibilidad[String(medico?.id)]).map(([fecha, horas], i) => (
                                    <div key={i}>
                                        <div className="date">
                                            {new Date(fecha + "T00:00:00-06:00").toLocaleDateString("es-ES")}
                                        </div>
                                        <div className="times">
                                            {horas.map((hora, j) => {
                                                const now = new Date();
                                                const fechaHora = new Date(`${fecha}T${hora}:00-06:00`);
                                                const isPast = fechaHora < now;

                                                const isConfirmed = confirmedAppointments.some(app =>
                                                    app.doctorId === medico?.id &&
                                                    app.date === fecha &&
                                                    app.time.slice(0,5) === hora.slice(0,5)
                                                );

                                                return (
                                                    <button
                                                        key={j}
                                                        onClick={() => !(isPast || isConfirmed) && handleTimeClick(fecha, hora)}
                                                        aria-disabled={isPast || isConfirmed}
                                                        className={`hora-btn ${isPast || isConfirmed ? 'disabled-time' : ''}`}
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
                            onClick={() => setSemanaOffset(prev => Math.min(prev + 1, 1))}
                            className="btn btn-primary"
                            disabled={semanaOffset >= 1}
                        >
                            Next →
                        </button>
                    </td>
                </tr>
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>Confirmar cita</h3>
                        <p><strong>Doctor:</strong> {medico?.nombre}</p>
                        <p><strong>Fecha:</strong> {new Date(selectedDate + "T00:00:00-06:00").toLocaleDateString("es-ES")}</p>
                        <p><strong>Hora:</strong> {selectedTime}</p>
                        <div className="modal-buttons">
                            <button onClick={handleConfirm}>Confirmar</button>
                            <button onClick={() => setShowModal(false)}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default HorarioExtendido;