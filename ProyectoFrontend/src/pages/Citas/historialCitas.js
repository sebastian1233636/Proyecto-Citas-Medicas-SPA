import React, { useState, useEffect, useContext } from "react";
import { AppContext } from '../../AppProvider';
import './historialPaciente.css';

function HistorialPaciente() {
    const backend = "http://localhost:8080";
    const [citas, setCitas] = useState([]);
    const [loading, setLoading] = useState(false);

    // Filtros
    const [status, setStatus] = useState("");
    const [doctor, setDoctor] = useState("");

    // Modal para mostrar notas
    const [modalVisible, setModalVisible] = useState(false);
    const [notasActuales, setNotasActuales] = useState("");
    const [citaSeleccionada, setCitaSeleccionada] = useState(null);

    const { authState } = useContext(AppContext);
    const user = authState.user;

    useEffect(() => {
        fetchHistorialCitas();
    }, []);

    const fetchHistorialCitas = async (statusFiltro = "", doctorFiltro = "") => {
        try {
            setLoading(true);
            const token = localStorage.getItem("token");
            let url = `${backend}/historialPaciente/historialPaciente/inicio`;

            // Construir parámetros de consulta
            const params = new URLSearchParams();
            if (statusFiltro) params.append("status", statusFiltro);
            if (doctorFiltro) params.append("doctor", doctorFiltro);

            if (params.toString()) {
                url += `?${params.toString()}`;
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
                setCitas(data);
            } else {
                console.error("Error al obtener el historial de citas");
            }
        } catch (err) {
            console.error("Error al obtener las citas:", err);
        } finally {
            setLoading(false);
        }
    };

    const handleStatusChange = (e) => {
        const nuevoValor = e.target.value;
        setStatus(nuevoValor);
        fetchHistorialCitas(nuevoValor, doctor);
    };

    const handleDoctorChange = (e) => {
        const nuevoValor = e.target.value;
        setDoctor(nuevoValor);
        fetchHistorialCitas(status, nuevoValor);
    };

    const verNotas = async (citaId) => {
        try {
            const token = localStorage.getItem("token");
            const response = await fetch(`${backend}/historialPaciente/${citaId}/notas`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                const notas = await response.text();
                setNotasActuales(notas || "No hay notas disponibles para esta cita.");
                setCitaSeleccionada(citaId);
                setModalVisible(true);
            } else {
                alert("Error al obtener las notas de la cita");
            }
        } catch (err) {
            console.error("Error al obtener notas:", err);
            alert("Error al obtener las notas");
        }
    };

    const aceptarCita = async (citaId) => {
        if (!window.confirm("¿Está seguro de que desea aceptar esta cita?")) {
            return;
        }

        try {
            const token = localStorage.getItem("token");
            const response = await fetch(`${backend}/historialPaciente/${citaId}/aceptar`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                alert("Cita aceptada exitosamente");
                fetchHistorialCitas(status, doctor); // Refrescar la lista
            } else {
                alert("Error al aceptar la cita");
            }
        } catch (err) {
            console.error("Error al aceptar cita:", err);
            alert("Error al aceptar la cita");
        }
    };

    const cancelarCita = async (citaId) => {
        if (!window.confirm("¿Está seguro de que desea cancelar esta cita?")) {
            return;
        }

        try {
            const token = localStorage.getItem("token");
            const response = await fetch(`${backend}/historialPaciente/${citaId}/cancelar`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                alert("Cita cancelada exitosamente");
                fetchHistorialCitas(status, doctor); // Refrescar la lista
            } else {
                alert("Error al cancelar la cita");
            }
        } catch (err) {
            console.error("Error al cancelar cita:", err);
            alert("Error al cancelar la cita");
        }
    };

    const getStatusBadgeClass = (status) => {
        switch (status.toLowerCase()) {
            case 'aceptada':
                return 'status-badge status-accepted';
            case 'cancelada':
                return 'status-badge status-cancelled';
            case 'pendiente':
                return 'status-badge status-pending';
            case 'completada':
                return 'status-badge status-completed';
            default:
                return 'status-badge status-default';
        }
    };

    return (
        <>

            <div className="search-bar">
                <form onSubmit={(e) => e.preventDefault()}>
                    <div className="input-container">
                        <div className="input-group">
                            <span className="input-label">Estado</span>
                            <select
                                id="status"
                                name="status"
                                value={status}
                                onChange={handleStatusChange}
                            >
                                <option value="">Todos</option>
                                <option value="Pendiente">Pendiente</option>
                                <option value="Aceptada">Aceptada</option>
                                <option value="Cancelada">Cancelada</option>
                                <option value="Completada">Completada</option>
                            </select>
                        </div>
                        <div className="input-group">
                            <span className="input-label">Doctor</span>
                            <input
                                type="text"
                                id="doctor"
                                name="doctor"
                                placeholder="Buscar por nombre del doctor"
                                value={doctor}
                                onChange={handleDoctorChange}
                            />
                        </div>
                        <button type="button" onClick={() => fetchHistorialCitas(status, doctor)}>
                            Buscar
                        </button>
                    </div>
                </form>
            </div>

            {loading ? (
                <div className="loading">Cargando historial...</div>
            ) : (
                <div className="historial-container">
                    {citas.length === 0 ? (
                        <div className="no-citas">
                            <p>No se encontraron citas con los filtros aplicados.</p>
                        </div>
                    ) : (
                        <table className="historial-table">
                            <thead>
                            <tr>
                                <th>Fecha</th>
                                <th>Hora</th>
                                <th>Doctor</th>
                                <th>Especialidad</th>
                                <th>Estado</th>
                                <th>Costo</th>
                                <th>Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            {citas.map((cita, index) => (
                                <tr key={index} className="historial-row">
                                    <td>{new Date(cita.fecha).toLocaleDateString("es-ES")}</td>
                                    <td>{cita.hora}</td>
                                    <td>{cita.medicoNombre}</td>
                                    <td>{cita.medicoEspecialidad}</td>
                                    <td>
                                            <span className={getStatusBadgeClass(cita.status)}>
                                                {cita.status}
                                            </span>
                                    </td>
                                    <td>${cita.medicoCosto}</td>
                                    <td className="actions-column">
                                        <div className="action-buttons">
                                            <button
                                                className="btn btn-info btn-sm"
                                                onClick={() => verNotas(cita.id)}
                                            >
                                                Ver Notas
                                            </button>
                                            {cita.status.toLowerCase() === 'pendiente' && (
                                                <>
                                                    <button
                                                        className="btn btn-success btn-sm"
                                                        onClick={() => aceptarCita(cita.id)}
                                                    >
                                                        Aceptar
                                                    </button>
                                                    <button
                                                        className="btn btn-danger btn-sm"
                                                        onClick={() => cancelarCita(cita.id)}
                                                    >
                                                        Cancelar
                                                    </button>
                                                </>
                                            )}
                                        </div>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )}
                </div>
            )}

            {/* Modal para mostrar notas */}
            {modalVisible && (
                <div className="modal-overlay" onClick={() => setModalVisible(false)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3>Notas de la Cita</h3>
                            <button
                                className="modal-close"
                                onClick={() => setModalVisible(false)}
                            >
                                ×
                            </button>
                        </div>
                        <div className="modal-body">
                            <p>{notasActuales}</p>
                        </div>
                        <div className="modal-footer">
                            <button
                                className="btn btn-secondary"
                                onClick={() => setModalVisible(false)}
                            >
                                Cerrar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default HistorialPaciente;