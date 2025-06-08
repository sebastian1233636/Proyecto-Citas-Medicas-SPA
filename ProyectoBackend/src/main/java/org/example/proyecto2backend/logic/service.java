package org.example.proyecto2backend.logic;


import org.example.proyecto2backend.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class service {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private CitaRepository citaRepository;

    public Iterable<Usuario> usuarioFindAll(){
        return usuarioRepository.findAll();
    }

    @Transactional
    public void registrarMedico(Medico medico){
        medicoRepository.save(medico);
    }

    public Iterable<Medico> medicoFindAll(){
        return medicoRepository.findAll();
    }

    @Transactional
    public void RegistrarUsuario(Usuario usuario){
        usuarioRepository.save(usuario);
    }

    public Iterable<Rol> rolFindAll(){
        return rolRepository.findAll();
    }

    public Usuario findUsuarioById(String id){
        return usuarioRepository.findById(id).get() ;
    }

    public boolean existeUsuarioPorId(String id){
        return usuarioRepository.existsById(id);
    }

    public Medico obtenerMedicoPorId(String id) {
        return medicoRepository.findById(id).orElse(null);
    }

    public List<Medico> FiltradoMedicos(String especialidad, String localidad) {
        if ((especialidad == null || especialidad.isEmpty()) && (localidad == null || localidad.isEmpty())) {
            return (List<Medico>) medicoRepository.findAll();
        }
        if (especialidad == null) especialidad = "";
        if (localidad == null) localidad = "";

        return medicoRepository.findByEspecialidadContainingIgnoreCaseAndLocalidadContainingIgnoreCase(especialidad, localidad);
    }

    public List<Medico> FiltradoMedicosPorStatus(String status) {
        if (status == null || status.isEmpty()) {
            return (List<Medico>) medicoRepository.findAll();
        }
        return medicoRepository.findByStatusContainingIgnoreCase(status);
    }

    public void actualizarMedico(Medico medico) {
        Medico medicoExistente = medicoRepository.findById(medico.getId())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + medico.getId()));
        double costoDouble = medico.getCosto().doubleValue();
        medicoExistente.setCosto(BigDecimal.valueOf(costoDouble));
        medicoExistente.setLocalidad(medico.getLocalidad());

        medicoRepository.save(medicoExistente);
    }

    @Transactional
    public void aceptarMedico(String id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + id));
        medico.setStatus("Aprobado");
        medicoRepository.save(medico);
    }

    public List<Horario> obtenerHorariosPorMedico(String medicoId) { return horarioRepository.findByMedicoId(medicoId); }

    public void agregarHorario(String medicoId, String dia, LocalTime horaInicio, LocalTime horaFin) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        if (horaFin.isBefore(horaInicio) || horaFin.equals(horaInicio)) {
            throw new IllegalArgumentException("La hora de finalización debe ser mayor a la de inicio.");
        }

        List<Horario> horariosExistentes = horarioRepository.findByMedicoIdAndDia(medicoId, dia);
        if (!horariosExistentes.isEmpty()) {
            throw new IllegalArgumentException("El médico ya tiene un horario asignado para el día " + dia);
        }
        Horario nuevoHorario = new Horario();
        nuevoHorario.setMedico(medico);
        nuevoHorario.setDia(dia);
        nuevoHorario.setHoraInicio(horaInicio);
        nuevoHorario.setHoraFin(horaFin);

        horarioRepository.save(nuevoHorario);
    }

    public void eliminarHorario(String id, String dia) {
        Optional<Horario> horario = horarioRepository.findById(id);
        if (horario.isPresent() && horario.get().getDia().equalsIgnoreCase(dia)) {
            horarioRepository.delete(horario.get());
        }
    }

    public void agendarCita(Cita cita){
        citaRepository.save(cita);
    }

    public List<Cita> obtenerCitasPorUsuario(String usuarioId) {
        return citaRepository.findByUsuarioId(usuarioId);
    }

    public Cita obtenerCitaPorId(String id){
        return citaRepository.findById(id).get();
    }

    public void actualizarCita(Cita cita) {
        Cita citaExistente = citaRepository.findById(String.valueOf(cita.getId()))
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + cita.getId()));
        citaExistente.setStatus(cita.getStatus());
        citaExistente.setMedico(cita.getMedico());
        citaExistente.setUsuario(cita.getUsuario());
        citaExistente.setFecha(cita.getFecha());
        citaExistente.setHora(cita.getHora());

        citaRepository.save(citaExistente);
    }

    public Iterable<Cita> obtenerCitasPorMedico(String id) {
        return citaRepository.findByMedicoId(id);
    }

    private boolean pacienteExisteEnBaseDeDatos(String paciente) {
        Usuario pacienteUsuario = usuarioRepository.findByNombreContainingIgnoreCase(paciente);
        return pacienteUsuario != null;
    }

    private boolean doctorExisteEnBaseDeDatos(String doctor) {
        Medico medico = medicoRepository.findByUsuarioNombreContainingIgnoreCase(doctor);
        return medico != null;
    }

    public Usuario encontrarUsuarioPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<LocalTime> obtenerHorariosOcupados(Medico medico, LocalDate fecha) {
        return citaRepository.findOcupadosByMedicoAndFecha(medico, fecha);
    }

    public Iterable<Cita> filtroHistorialPaciente(String status, String id, String idpaciente) {
        if (id != null && !id.isEmpty()) {
            if (status != null && !status.isEmpty()) {
                return citaRepository.findByStatusAndMedicoId(status, id);
            }
            return citaRepository.findByMedicoId(id);
        }

        if (status != null && !status.isEmpty()) {
            return citaRepository.findByStatus(status);
        }

        return citaRepository.findByUsuarioId(idpaciente);
    }

    public Iterable<Cita> filtroHistorialMedico(String status, String userid, String idDoctor) {
        if (userid != null && !userid.isEmpty()) {
            if (status != null && !status.isEmpty()) {
                return citaRepository.findByStatusAndUsuarioId(status, userid);
            }
            return citaRepository.findByUsuarioId(userid);
        }

        if (status != null && !status.isEmpty()) {
            return citaRepository.findByStatus(status);
        }

        return citaRepository.findByMedicoId(idDoctor);
    }

    public void aceptarCita(String citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setStatus("Aceptada");
        citaRepository.save(cita);
    }

    public void cancelarCita(String citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setStatus("Cancelada");
        citaRepository.save(cita);
    }

    public String obtenerNotasCita(String citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        return cita.getNotas(); // Suponiendo que existe un campo "notas"
    }

    public List<Cita> obtenerCitasMedico(String medicoId, String status, String nombrePaciente) {
        List<Cita> citas;

        if (status != null && !status.isEmpty() && nombrePaciente != null && !nombrePaciente.isEmpty()) {
            // Filtrar por ambos criterios
            citas = citaRepository.findByStatusAndMedicoIdAndUsuarioNombreContainingIgnoreCase(
                    status, medicoId, nombrePaciente);
        } else if (status != null && !status.isEmpty()) {
            // Filtrar solo por estado
            citas = (List<Cita>) citaRepository.findByStatusAndMedicoId(status, medicoId);
        } else if (nombrePaciente != null && !nombrePaciente.isEmpty()) {
            // Filtrar solo por nombre del paciente
            citas = (List<Cita>) citaRepository.findByUsuarioNombreContainingIgnoreCaseAndMedicoId(
                    nombrePaciente, medicoId);
        } else {
            // Sin filtros
            citas = (List<Cita>) citaRepository.findByMedicoId(medicoId);
        }

        return citas.stream()
                .sorted((c1, c2) -> {
                    int fechaComparison = c2.getFecha().compareTo(c1.getFecha());
                    if (fechaComparison != 0) {
                        return fechaComparison;
                    }
                    return c2.getHora().compareTo(c1.getHora());
                })
                .toList();
    }

    public List<Cita> obtenerCitasPaciente(String usuarioId, String status, String nombreMedico) {
        List<Cita> citas;

        if (status != null && !status.isEmpty() && nombreMedico != null && !nombreMedico.isEmpty()) {
            citas = citaRepository.findByStatusAndUsuarioIdAndMedicoUsuarioNombreContainingIgnoreCase(
                    status, usuarioId, nombreMedico);
        } else if (status != null && !status.isEmpty()) {
            citas = (List<Cita>) citaRepository.findByStatusAndUsuarioId(status, usuarioId);
        } else if (nombreMedico != null && !nombreMedico.isEmpty()) {
            citas = (List<Cita>) citaRepository.findByMedicoUsuarioNombreContainingIgnoreCaseAndUsuarioId(
                    nombreMedico, usuarioId);
        } else {
            citas = citaRepository.findByUsuarioId(usuarioId);
        }

        return citas.stream()
                .sorted((c1, c2) -> {
                    int fechaComparison = c2.getFecha().compareTo(c1.getFecha());
                    if (fechaComparison != 0) {
                        return fechaComparison;
                    }
                    return c2.getHora().compareTo(c1.getHora());
                })
                .toList();
    }

    public Cita obtenerCitaPorId(Integer citaId) {
        return citaRepository.findById(String.valueOf(citaId)).orElse(null);
    }

    public void completarCita(Integer citaId, String nuevoStatus, String notas) {
        Cita cita = citaRepository.findById(String.valueOf(citaId))
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setStatus(nuevoStatus);
        cita.setNotas(notas);

        citaRepository.save(cita);
    }

    public List<Medico> ObtenerMedicosPendientes() {
        return medicoRepository.findByStatus("Pendiente");
    }

    public Usuario buscarUsuario(String username) {
        return usuarioRepository.findByNombreContainingIgnoreCase(username);
    }

    public Medico actualizarMedico(String id, String especialidad, BigDecimal costo, String localidad, Integer frecuencia) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + id));

        medico.setEspecialidad(especialidad);
        medico.setCosto(costo);
        medico.setLocalidad(localidad);
        medico.setFrecuenciaCitas(frecuencia);

        return medicoRepository.save(medico);
    }

    public List<Horario> obtenerHorariosPorMedico(Integer medicoId) {
        return horarioRepository.findByMedicoId(medicoId.toString()); // o ajusta según el tipo que uses
    }

    public Horario agregarHorario(Integer medicoId, String dia, LocalTime horaInicio, LocalTime horaFin) {
        Medico medico = medicoRepository.findById(medicoId.toString())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + medicoId));

        if (horaFin.isBefore(horaInicio) || horaFin.equals(horaInicio)) {
            throw new IllegalArgumentException("La hora de finalización debe ser mayor a la de inicio.");
        }

        List<Horario> horariosExistentes = horarioRepository.findByMedicoIdAndDia(medicoId.toString(), dia);
        if (!horariosExistentes.isEmpty()) {
            throw new IllegalArgumentException("El médico ya tiene un horario asignado para el día " + dia);
        }

        Horario nuevoHorario = new Horario();
        nuevoHorario.setMedico(medico);
        nuevoHorario.setDia(dia);
        nuevoHorario.setHoraInicio(horaInicio);
        nuevoHorario.setHoraFin(horaFin);

        return horarioRepository.save(nuevoHorario);
    }

    public void eliminarHorario(Integer id, String dia) {
        Optional<Horario> horario = horarioRepository.findById(id.toString());
        if (horario.isPresent() && horario.get().getDia().equalsIgnoreCase(dia)) {
            horarioRepository.delete(horario.get());
        }
    }


}