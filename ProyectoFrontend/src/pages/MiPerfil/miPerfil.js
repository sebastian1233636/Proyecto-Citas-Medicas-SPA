import "./miPerfil.css";
import React, { useEffect, useState } from "react";

const backend = "http://localhost:8080";

const MiPerfil = () => {
    const [perfil, setPerfil] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const [mensajeError, setMensajeError] = useState("");
    const [mensajeExito, setMensajeExito] = useState("");
    const [formData, setFormData] = useState({
        especialidad: "",
        costo: "",
        localidad: "",
        frecuenciaCitas: ""
    });

    const fetchPerfil = async () => {
        try {
            const token = localStorage.getItem("token");
            const response = await fetch(`${backend}/api/perfil/miPerfil`, {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                const data = await response.json();
                setPerfil(data);
                setFormData({
                    especialidad: data.especialidad ?? "",
                    costo: data.costo !== null && data.costo !== undefined ? data.costo : "",
                    localidad: data.localidad ?? "",
                    frecuenciaCitas: data.frecuenciaCitas !== null && data.frecuenciaCitas !== undefined ? data.frecuenciaCitas : ""
                });
            } else {
                setError(true);
            }
        } catch (err) {
            setError(true);
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleActualizar = async (e) => {
        e.preventDefault();
        setMensajeError("");
        setMensajeExito("");

        try {
            const token = localStorage.getItem("token");
            const response = await fetch(`${backend}/api/perfil/actualizar`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({
                    id: perfil.id,
                    ...formData
                })
            });
            if (response.ok) {
                await fetchPerfil();
                setMensajeExito("Perfil actualizado correctamente.");
            } else {
                const res = await response.json();
                setMensajeError(res.message || "Error al actualizar");
            }
        } catch (err) {
            setMensajeError("Error de red al actualizar.");
        }
    };

    const eliminarHorario = async (id, dia) => {
        const token = localStorage.getItem("token");
        try {
            const response = await fetch(`${backend}/api/perfil/eliminarHorario/${id}/${dia}`, {
                method: "DELETE",
                headers: { "Authorization": `Bearer ${token}` }
            });
            if (response.ok) {
                await fetchPerfil();
            }
        } catch (err) {
            console.error("Error al eliminar horario");
        }
    };

    const agregarHorario = async (e) => {
        e.preventDefault();
        setMensajeError("");

        const dia = e.target.horarioDia.value;
        const horaInicio = e.target.horaInicioHorario.value;
        const horaFin = e.target.horaFinHorario.value;
        
        if (horaFin <= horaInicio) {
            setMensajeError("La hora de fin debe ser mayor que la hora de inicio.");
            console.log("Error: hora fin <= hora inicio");
            return;
        }

        const normalizarHora = (horaStr) => {
            return horaStr.slice(0, 5);
        };

        const existeHorario = perfil.horarios.some(horario =>
            horario.dia === dia &&
            normalizarHora(horario.horaInicio) === horaInicio &&
            normalizarHora(horario.horaFin) === horaFin
        );

        if (existeHorario) {
            setMensajeError("Este horario ya existe.");
            return;
        }

        const token = localStorage.getItem("token");
        try {
            const response = await fetch(`${backend}/api/perfil/agregarHorario/${perfil.id}/${dia}/${horaInicio}/${horaFin}`, {
                method: "POST",
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (response.ok) {
                await fetchPerfil();
                e.target.reset();
                console.log("Horario agregado OK");
            } else {
                const res = await response.json();
                setMensajeError(res.message || "Error al agregar horario");
                console.log("Error backend:", res.message);
            }
        } catch (err) {
            setMensajeError("Error de red al agregar horario");
            console.error("Catch error red:", err);
        }
    };

    useEffect(() => {
        fetchPerfil();
    }, []);

    useEffect(() => {
        if (mensajeExito) {
            const timer = setTimeout(() => {
                setMensajeExito("");
            }, 2000);
            return () => clearTimeout(timer);
        }
    }, [mensajeExito]);

    useEffect(() => {
        if (mensajeError) {
            const timer = setTimeout(() => {
                setMensajeError("");
            }, 2000);
            return () => clearTimeout(timer);
        }
    }, [mensajeError]);

    if (loading) return <p className="miPerfilMod-centerMessage">Cargando perfil...</p>;
    if (error || !perfil) return <p className="miPerfilMod-centerMessage miPerfilMod-centerMessage-error">Error al cargar perfil.</p>;

    return (
        <div className="miPerfilMod-container">
            <div className="miPerfilMod-card">
                <img
                    src={`${backend}/user/imagen/${perfil.id}`}
                    alt="Foto de perfil"
                    className="miPerfilMod-photo"
                />
                <h2 className="miPerfilMod-nombre">{perfil.nombre}</h2>

                {perfil.especialidad &&  (
                    <>
                        <form onSubmit={handleActualizar} className="miPerfilMod-form">
                            <input type="hidden" name="id" value={perfil.id} />

                            <div className="miPerfilMod-form-group">
                                <label htmlFor="especialidadMod">Especialidad:</label>
                                <input
                                    type="text"
                                    id="especialidadMod"
                                    name="especialidad"
                                    value={formData.especialidad}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>

                            <div className="miPerfilMod-form-group">
                                <label htmlFor="costoMod">Costo:</label>
                                <input
                                    type="number"
                                    id="costoMod"
                                    name="costo"
                                    value={formData.costo}
                                    onChange={handleInputChange}
                                    min="0"
                                    required
                                />
                            </div>

                            <div className="miPerfilMod-form-group">
                                <label htmlFor="localidadMod">Localidad:</label>
                                <input
                                    type="text"
                                    id="localidadMod"
                                    name="localidad"
                                    value={formData.localidad}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>

                            <div className="miPerfilMod-form-group">
                                <label htmlFor="frecuenciaMod">Frecuencia:</label>
                                <input
                                    type="number"
                                    id="frecuenciaMod"
                                    name="frecuenciaCitas"
                                    value={formData.frecuenciaCitas}
                                    onChange={handleInputChange}
                                    min="1"
                                    required
                                />
                            </div>

                            <div className="miPerfilMod-btn-container">
                                <button type="submit" className="miPerfilMod-btn">Actualizar</button>
                            </div>
                        </form>

                        {mensajeExito && <div className="miPerfilMod-success-message"><p>{mensajeExito}</p></div>}

                        <h3 className="miPerfilMod-subtitle">Horarios de Atención</h3>
                        <table className="miPerfilMod-table">
                            <thead>
                            <tr>
                                <th>Día</th>
                                <th>Hora Inicio</th>
                                <th>Hora Fin</th>
                                <th>Acción</th>
                            </tr>
                            </thead>
                            <tbody>
                            {perfil.horarios.map(horario => (
                                <tr key={horario.horarioId}>
                                    <td>{horario.dia}</td>
                                    <td>{horario.horaInicio}</td>
                                    <td>{horario.horaFin}</td>
                                    <td>
                                        <button
                                            className="miPerfilMod-btn-delete"
                                            onClick={() => eliminarHorario(horario.horarioId, horario.dia)}
                                        >
                                            Eliminar
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>

                        <h3 className="miPerfilMod-subtitle">Agregar Nuevo Horario</h3>
                        <form onSubmit={agregarHorario} className="miPerfilMod-form">
                            <input type="hidden" name="medicoId" value={perfil.id} />

                            <div className="miPerfilMod-form-group">
                                <label htmlFor="diaHorarioMod">Día:</label>
                                <select id="diaHorarioMod" name="horarioDia">
                                    <option value="Lunes">Lunes</option>
                                    <option value="Martes">Martes</option>
                                    <option value="Miércoles">Miércoles</option>
                                    <option value="Jueves">Jueves</option>
                                    <option value="Viernes">Viernes</option>
                                    <option value="Sábado">Sábado</option>
                                    <option value="Domingo">Domingo</option>
                                </select>
                            </div>

                            <div className="miPerfilMod-form-group">
                                <label htmlFor="horaInicioHorarioMod">Hora Inicio:</label>
                                <input type="time" id="horaInicioHorarioMod" name="horaInicioHorario" required />
                            </div>

                            <div className="miPerfilMod-form-group">
                                <label htmlFor="horaFinHorarioMod">Hora Fin:</label>
                                <input type="time" id="horaFinHorarioMod" name="horaFinHorario" required />
                            </div>

                            <div className="miPerfilMod-btn-container">
                                <button type="submit" className="miPerfilMod-btn">Actualizar</button>
                            </div>
                        </form>

                        {mensajeError && <div className="miPerfilMod-error-message"><p>{mensajeError}</p></div>}
                    </>
                )}
            </div>
        </div>
    );
};

export default MiPerfil;