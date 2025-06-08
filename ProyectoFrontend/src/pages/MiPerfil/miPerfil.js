import "./miPerfil.css";
import React, { useEffect, useState } from "react";

const backend = "http://localhost:8080";

const MiPerfil = () => {
    const [perfil, setPerfil] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const [mensajeError, setMensajeError] = useState("");

    const fetchPerfil = async () => {
        setLoading(true);
        setError(false);
        try {
            const token = localStorage.getItem("token");
            const response = await fetch(backend + "/api/perfil/miPerfil", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
            });
            if (response.ok) {
                const data = await response.json();
                setPerfil(data);
            } else {
                setError(true);
            }
        } catch (err) {
            console.error("Error al cargar el perfil:", err);
            setError(true);
        } finally {
            setLoading(false);
        }
    };

    const eliminarHorario = async (id, dia) => {
        const token = localStorage.getItem("token");
        try {
            const response = await fetch(`${backend}/api/perfil/eliminarHorario/${id}/${dia}`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                await fetchPerfil();
            } else {
                console.error("Error al eliminar horario");
            }
        } catch (err) {
            console.error("Error de red al eliminar horario:", err);
        }
    };

    const agregarHorario = async (e) => {
        e.preventDefault();
        setMensajeError("");
        const token = localStorage.getItem("token");

        const dia = e.target.dia.value;
        const horaInicio = e.target.horaInicio.value.slice(0, 5);
        const horaFin = e.target.horaFin.value.slice(0, 5);

        if (horaFin <= horaInicio) {
            setMensajeError("La hora de fin debe ser mayor que la hora de inicio.");
            return;
        }

        try {
            const response = await fetch(`${backend}/api/perfil/agregarHorario/${perfil.id}/${dia}/${horaInicio}/${horaFin}`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                await fetchPerfil();
                e.target.reset();
            } else {
                const errorData = await response.json();
                setMensajeError(errorData.message || "Error al agregar horario.");
            }
        } catch (err) {
            console.error("Error de red al agregar horario:", err);
            setMensajeError("Error de red al agregar horario.");
        }
    };

    useEffect(() => {
        fetchPerfil();
    }, []);

    if (loading) return <p className="loading-text">Cargando perfil...</p>;
    if (error) return <p className="error-text">Error al cargar el perfil.</p>;

    return (
        <section className="perfil-container">
            <header className="perfil-header">
                <img
                    src={`http://localhost:8080/user/imagen/${perfil.id}`}
                    alt="Foto del usuario"
                    className="perfil-foto"
                />
                <h1 className="perfil-nombre">{perfil.nombre}</h1>
            </header>

            {perfil.especialidad && (
                <>
                    <section className="datos-medico">
                        <h2>Información del Médico</h2>
                        <p><strong>Especialidad:</strong> {perfil.especialidad}</p>
                        <p><strong>Costo por Consulta:</strong> ₡{perfil.costo}</p>
                        <p><strong>Localidad:</strong> {perfil.localidad}</p>
                        <p><strong>Frecuencia de Citas:</strong> Cada {perfil.frecuenciaCitas} días</p>
                    </section>

                    <section className="horarios-atencion">
                        <h2>Horarios de Atención</h2>
                        <table className="tabla-horarios">
                            <thead>
                            <tr>
                                <th>Día</th>
                                <th>Hora Inicio</th>
                                <th>Hora Fin</th>
                                <th>Acción</th>
                            </tr>
                            </thead>
                            <tbody>
                            {perfil.horarios.map((horario) => (
                                <tr key={horario.horarioId}>
                                    <td>{horario.dia}</td>
                                    <td>{horario.horaInicio}</td>
                                    <td>{horario.horaFin}</td>
                                    <td>
                                        <button
                                            onClick={() => eliminarHorario(horario.horarioId, horario.dia)}
                                            className="btn-eliminar"
                                        >
                                            Eliminar
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </section>

                    <section className="form-agregar-horario">
                        <h2>Agregar Nuevo Horario</h2>
                        <form onSubmit={agregarHorario} className="formulario-horario">
                            <input type="hidden" name="medicoId" value={perfil.id} />

                            <div className="grupo-formulario">
                                <label htmlFor="dia">Día:</label>
                                <select id="dia" name="dia">
                                    <option value="Lunes">Lunes</option>
                                    <option value="Martes">Martes</option>
                                    <option value="Miércoles">Miércoles</option>
                                    <option value="Jueves">Jueves</option>
                                    <option value="Viernes">Viernes</option>
                                    <option value="Sábado">Sábado</option>
                                    <option value="Domingo">Domingo</option>
                                </select>
                            </div>

                            <div className="grupo-formulario">
                                <label htmlFor="horaInicio">Hora de Inicio:</label>
                                <input type="time" id="horaInicio" name="horaInicio" required />
                            </div>

                            <div className="grupo-formulario">
                                <label htmlFor="horaFin">Hora de Fin:</label>
                                <input type="time" id="horaFin" name="horaFin" required />
                            </div>

                            <div className="contenedor-boton">
                                <button type="submit" className="btn-agregar">
                                    Agregar Horario
                                </button>
                            </div>

                            {mensajeError && <p className="mensaje-error">{mensajeError}</p>}
                        </form>
                    </section>
                </>
            )}
        </section>
    );
};

export default MiPerfil;
