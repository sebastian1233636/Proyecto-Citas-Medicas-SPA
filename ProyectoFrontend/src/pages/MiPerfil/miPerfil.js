import { AppContext } from "../../AppProvider";
import { useNavigate } from "react-router-dom";

import "./miPerfil.css";
import React, { useEffect, useState } from "react";

const backend = "http://localhost:8080"; // Cambia esto si tu backend usa otra URL/puerto

const Perfil = () => {
    const [perfil, setPerfil] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);

    const fetchPerfil = async () => {
        setLoading(true);
        setError(false);

        try {
            const token = localStorage.getItem("token");
            const response = await fetch(backend + "/api/perfil/miPerfil", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    // Si usas autenticación por token, agrega aquí:
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
                await fetchPerfil(); // recargar datos actualizados
            } else {
                console.error("Error al eliminar horario");
            }
        } catch (err) {
            console.error("Error de red al eliminar horario:", err);
        }
    };




    const agregarHorario = async (e) => {
        e.preventDefault();
        const token = localStorage.getItem("token");

        const dia = e.target.dia.value;
        const horaInicio = e.target.horaInicio.value.slice(0, 5); // solo HH:mm
        const horaFin = e.target.horaFin.value.slice(0, 5);       // solo HH:mm

        try {
            const response = await fetch(`${backend}/api/perfil/agregarHorario/${perfil.id}/${dia}/${horaInicio}/${horaFin}`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });
            console.log("Agregando horario:", perfil.id, dia, horaInicio, horaFin);


            if (response.ok) {
                await fetchPerfil(); // recargar
                e.target.reset();    // limpiar el formulario
            } else {
                console.error("Error al agregar horario");
            }
        } catch (err) {
            console.error("Error de red al agregar horario:", err);
        }
    };





    useEffect(() => {
        fetchPerfil();
    }, []);

    if (loading) return <p>Cargando...</p>;
    if (error) return <p>Error al cargar el perfil.</p>;

    return (
        <div className="perfil">
            <h2>{perfil.nombre}</h2>

            {perfil.especialidad && (
                <>
                    <h3>Datos del Médico</h3>
                    <p><strong>Especialidad:</strong> {perfil.especialidad}</p>
                    <p><strong>Costo:</strong> ₡{perfil.costo}</p>
                    <p><strong>Localidad:</strong> {perfil.localidad}</p>
                    <p><strong>Frecuencia de Citas:</strong> {perfil.frecuenciaCitas} días</p>

                    <h3>Horarios de Atención</h3>
                    <table>
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
                                    <button onClick={() => eliminarHorario(horario.horarioId, horario.dia)} className="btn-delete">
                                        Eliminar
                                    </button>

                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    <h3>Agregar Nuevo Horario</h3>
                    <form onSubmit={agregarHorario}>
                        <input type="hidden" name="medicoId" value={perfil.id}/>

                        <div className="form-group">
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

                        <div className="form-group">
                            <label htmlFor="horaInicio">Hora Inicio:</label>
                            <input type="time" id="horaInicio" name="horaInicio" required/>
                        </div>

                        <div className="form-group">
                            <label htmlFor="horaFin">Hora Fin:</label>
                            <input type="time" id="horaFin" name="horaFin" required/>
                        </div>

                        <div className="btn-container">
                            <button type="submit" className="btn">Agregar Horario</button>
                        </div>
                    </form>

                </>
            )}
        </div>
    );
}

    export default Perfil;
