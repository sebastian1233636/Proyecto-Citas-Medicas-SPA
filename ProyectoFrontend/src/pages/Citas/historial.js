import React, { useState, useEffect, useContext } from "react";
import { AppContext } from '../../AppProvider';
import { useNavigate } from "react-router-dom";
import './historial.css';

const Historial = () => {
    const [citas, setCitas] = useState([]);
    const [filtros, setFiltros] = useState({
        status: '',
        nombre: ''
    });
    const [citaSeleccionada, setCitaSeleccionada] = useState(null);
    const [modalCompletarVisible, setModalCompletarVisible] = useState(false);
    const [notasCita, setNotasCita] = useState('');
    const [statusCita, setStatusCita] = useState('Completada');
    const [loading, setLoading] = useState(false);
    const [authLoading, setAuthLoading] = useState(true);

    const { authState } = useContext(AppContext);
    const navigate = useNavigate();
    const backend = "http://localhost:8080";

    // Verificar autenticación al montar el componente
    useEffect(() => {
        const token = localStorage.getItem("token");

        if (!token) {
            navigate('/login');
            return;
        }

        // Si hay token y usuario, todo está bien
        if (authState.user) {
            setAuthLoading(false);
            return;
        }

        // Si hay token pero no usuario, esperar más tiempo para que el contexto se cargue
        // No redirigir automáticamente, dejar que el AppProvider maneje la validación
        const timeoutId = setTimeout(() => {
            setAuthLoading(false);
        }, 3000); // Aumentar a 3 segundos

        return () => clearTimeout(timeoutId);
    }, [authState.user, navigate]);

    const esMedico = authState.user?.rol === 2;
    const userId = authState.user?.id;

    useEffect(() => {
        if (userId && !authLoading) {
            fetchCitas();
        }
    }, [userId, filtros, authLoading]);

    const fetchCitas = async () => {
        try {
            setLoading(true);
            const token = localStorage.getItem("token");

            if (!token) {
                navigate('/login');
                return;
            }

            let url;
            const params = new URLSearchParams();

            if (filtros.status) {
                params.append("status", filtros.status);
            }

            if (esMedico) {
                url = `${backend}/Historial/medico/${userId}`;
                if (filtros.nombre) {
                    params.append("nombrePaciente", filtros.nombre);
                }
            } else {
                url = `${backend}/Historial/paciente/${userId}`;
                if (filtros.nombre) {
                    params.append("nombreMedico", filtros.nombre);
                }
            }

            if (params.toString()) {
                url += `?${params.toString()}`;
            }

            const headers = {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            };

            const response = await fetch(url, {
                method: "GET",
                headers: headers
            });

            if (response.ok) {
                const data = await response.json();
                setCitas(data);
            } else if (response.status === 404) {
                setCitas([]);
            } else if (response.status === 401) {
                // Token expirado o inválido
                localStorage.removeItem("token");
                navigate('/login');
            } else {
                throw new Error("Error al obtener las citas");
            }

        } catch (error) {
            console.error("Error al obtener citas:", error);
            setCitas([]);
        } finally {
            setLoading(false);
        }
    };

    const handleFiltroChange = (campo, valor) => {
        setFiltros(prev => ({
            ...prev,
            [campo]: valor
        }));
    };

    const limpiarFiltros = () => {
        setFiltros({
            status: '',
            nombre: ''
        });
    };

    const abrirModalCompletar = (cita) => {
        setCitaSeleccionada(cita);
        setNotasCita(cita.notas || '');
        setStatusCita(cita.status === 'Pendiente' ? 'Completada' : cita.status);
        setModalCompletarVisible(true);
    };

    const completarCita = async () => {
        try {
            const token = localStorage.getItem("token");

            if (!token) {
                navigate('/login');
                return;
            }

            const url = `${backend}/Historial/completar/${citaSeleccionada.id}`;

            const response = await fetch(url, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({
                    status: statusCita,
                    notas: notasCita
                })
            });

            if (response.ok) {
                alert("Cita actualizada exitosamente");
                setModalCompletarVisible(false);
                fetchCitas();
            } else if (response.status === 401) {
                localStorage.removeItem("token");
                navigate('/login');
            } else {
                throw new Error("Error al actualizar la cita");
            }

        } catch (error) {
            console.error("Error al completar cita:", error);
            alert("Error al actualizar la cita");
        }
    };

    const formatearFecha = (fecha) => {
        return new Date(fecha).toLocaleDateString("es-ES", {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };

    const getStatusClass = (status) => {
        switch (status?.toLowerCase()) {
            case 'completada':
                return 'status-completada';
            case 'pendiente':
                return 'status-pendiente';
            case 'cancelada':
                return 'status-cancelada';
            default:
                return 'status-default';
        }
    };

    // Mostrar loading mientras se verifica la autenticación
    if (authLoading) {
        return (
            <div className="historial-container">
                <div className="loading">Loading user data...</div>
            </div>
        );
    }

    // Si no hay token, redirigir
    const token = localStorage.getItem("token");
    if (!token) {
        navigate('/login');
        return null;
    }

    // Si hay token pero no hay usuario, mostrar que está cargando
    // (el AppProvider debería estar validando el token)
    if (!authState.user) {
        return (
            <div className="historial-container">
                <div className="loading">Validating session...</div>
            </div>
        );
    }

    return (
        <div className="historial-container">
            <h2>{esMedico ? 'Doctor' : 'Patient'} - <span className="patient-name">{authState.user.name || 'Usuario'}</span> - {esMedico ? 'appointments' : 'appointment history'}</h2>

            <div className="filtros-container">
                <div className="filtro-grupo">
                    <label htmlFor="status">Status:</label>
                    <select
                        id="status"
                        value={filtros.status}
                        onChange={(e) => handleFiltroChange('status', e.target.value)}
                    >
                        <option value="">All</option>
                        <option value="Pendiente">Pending</option>
                        <option value="Completada">Completed</option>
                        <option value="Cancelada">Cancelled</option>
                    </select>
                </div>

                <div className="filtro-grupo">
                    <label htmlFor="nombre">
                        {esMedico ? 'Patient:' : 'Doctor:'}
                    </label>
                    <input
                        type="text"
                        id="nombre"
                        value={filtros.nombre}
                        onChange={(e) => handleFiltroChange('nombre', e.target.value)}
                        placeholder={esMedico ? 'Search patient...' : 'Search doctor...'}
                    />
                </div>

                <button className="btn-search" onClick={() => fetchCitas()}>
                    Search
                </button>

                <button className="btn-clear" onClick={limpiarFiltros}>
                    Clear Filters
                </button>
            </div>

            {/* Lista de Citas */}
            {loading ? (
                <div className="loading">Loading appointments...</div>
            ) : citas.length === 0 ? (
                <div className="no-citas">
                    No appointments found with the specified criteria.
                </div>
            ) : (
                <div className="citas-lista">
                    {citas.map((cita) => (
                        <div key={cita.id} className="cita-card">
                            <div className="doctor-avatar">
                                {esMedico ?
                                    cita.nombrePaciente?.charAt(0).toUpperCase() :
                                    cita.nombreMedico?.charAt(0).toUpperCase()
                                }
                            </div>
                            <div className="cita-info">
                                <div className="cita-details">
                                    <div className="doctor-name">
                                        {esMedico ? cita.nombrePaciente : cita.nombreMedico}
                                    </div>
                                    <div className="doctor-specialty">
                                        {esMedico ? 'Patient' : 'Doctor'}
                                    </div>
                                    {cita.notas && (
                                        <div className="appointment-location">
                                            {cita.notas.length > 50 ?
                                                `${cita.notas.substring(0, 50)}...` :
                                                cita.notas
                                            }
                                        </div>
                                    )}
                                </div>
                                <div className="cita-datetime">
                                    <div className="appointment-date">
                                        {formatearFecha(cita.fecha)}
                                    </div>
                                    <div className="appointment-time">
                                        {cita.hora}
                                    </div>
                                </div>
                                <div className="status-actions">
                                    <span className={`status-badge ${getStatusClass(cita.status)}`}>
                                        {cita.status === 'Pendiente' ? 'Pending' :
                                            cita.status === 'Completada' ? 'Completed' :
                                                cita.status === 'Cancelada' ? 'Cancelled' :
                                                    cita.status}
                                    </span>
                                    {esMedico && (
                                        <div className="action-icons">
                                            <div
                                                className="action-icon icon-view"
                                                title="View"
                                                onClick={() => abrirModalCompletar(cita)}
                                            >
                                                👁
                                            </div>
                                            {cita.status !== 'Cancelada' && (
                                                <div
                                                    className="action-icon icon-edit"
                                                    title="Edit"
                                                    onClick={() => abrirModalCompletar(cita)}
                                                >
                                                    ✓
                                                </div>
                                            )}
                                            {cita.status === 'Cancelada' && (
                                                <div
                                                    className="action-icon icon-cancel"
                                                    title="Cancelled"
                                                >
                                                    ✕
                                                </div>
                                            )}
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {/* Modal para completar/editar cita (solo médicos) */}
            {modalCompletarVisible && citaSeleccionada && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>
                            {citaSeleccionada.status === 'Pendiente' ? 'Complete Appointment' : 'Edit Appointment'}
                        </h3>

                        <div className="modal-info">
                            <p><strong>{esMedico ? 'Patient:' : 'Doctor:'}</strong> {esMedico ? citaSeleccionada.nombrePaciente : citaSeleccionada.nombreMedico}</p>
                            <p><strong>Date:</strong> {formatearFecha(citaSeleccionada.fecha)}</p>
                            <p><strong>Time:</strong> {citaSeleccionada.hora}</p>
                        </div>

                        <div className="modal-form">
                            <div className="form-grupo">
                                <label htmlFor="statusModal">Status:</label>
                                <select
                                    id="statusModal"
                                    value={statusCita}
                                    onChange={(e) => setStatusCita(e.target.value)}
                                >
                                    <option value="Completada">Completed</option>
                                    <option value="Cancelada">Cancelled</option>
                                    <option value="Pendiente">Pending</option>
                                </select>
                            </div>

                            <div className="form-grupo">
                                <label htmlFor="notasModal">Notes:</label>
                                <textarea
                                    id="notasModal"
                                    value={notasCita}
                                    onChange={(e) => setNotasCita(e.target.value)}
                                    placeholder="Add notes about the consultation..."
                                    rows="4"
                                />
                            </div>
                        </div>

                        <div className="modal-buttons">
                            <button
                                className="btn btn-success"
                                onClick={completarCita}
                            >
                                Save
                            </button>
                            <button
                                className="btn btn-secondary"
                                onClick={() => setModalCompletarVisible(false)}
                            >
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Historial;