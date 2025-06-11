package org.example.proyecto2backend.logic;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.proyecto2backend.data.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Optional;
import java.util.List;

@Service
public class service {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private CitaRepository citaRepository;

    /* Metodos de médico en el Service */
    public Iterable<Medico> medicoFindAll(){
        return medicoRepository.findAll();
    }

    public Medico obtenerMedicoPorId(String id) {
        return medicoRepository.findById(id).orElse(null);
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

    public List<Medico> FiltradoMedicos(String especialidad, String localidad) {
        if ((especialidad == null || especialidad.isEmpty()) && (localidad == null || localidad.isEmpty())) {
            return medicoRepository.findAll();
        }
        if (especialidad == null) especialidad = "";
        if (localidad == null) localidad = "";

        return medicoRepository.findByEspecialidadContainingIgnoreCaseAndLocalidadContainingIgnoreCase(especialidad, localidad);
    }

    public List<Medico> ObtenerMedicosPendientes() {
        return medicoRepository.findByStatus("Pendiente");
    }


    /* Metodos de usuario en el Service */
    public Usuario findUsuarioById(String id){
        return usuarioRepository.findById(id).get() ;
    }


    /* Metodos de Cita en el Service */
    public void agendarCita(Cita cita){
        citaRepository.save(cita);
    }
    public List<Cita> obtenerCitasMedico(String medicoId, String status, String nombrePaciente) {
        List<Cita> citas;
        if (status != null && !status.isEmpty() && nombrePaciente != null && !nombrePaciente.isEmpty()) {
            citas = citaRepository.findByStatusAndMedicoIdAndUsuarioNombreContainingIgnoreCase(
                    status, medicoId, nombrePaciente);
        } else if (status != null && !status.isEmpty()) {
            citas = (List<Cita>) citaRepository.findByStatusAndMedicoId(status, medicoId);
        } else if (nombrePaciente != null && !nombrePaciente.isEmpty()) {
            citas = citaRepository.findByUsuarioNombreContainingIgnoreCaseAndMedicoId(
                    nombrePaciente, medicoId);
        } else { citas = (List<Cita>) citaRepository.findByMedicoId(medicoId); }
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
            citas = citaRepository.findByMedicoUsuarioNombreContainingIgnoreCaseAndUsuarioId(
                    nombreMedico, usuarioId);
        } else { citas = citaRepository.findByUsuarioId(usuarioId); }
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

    public Cita obtenerCitaPorId(Integer citaId) { return citaRepository.findById(String.valueOf(citaId)).orElse(null); }

    public void completarCita(Integer citaId, String nuevoStatus, String notas) {
        Cita cita = citaRepository.findById(String.valueOf(citaId))
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setStatus(nuevoStatus);
        cita.setNotas(notas);

        citaRepository.save(cita);
    }

    /* Metodos de Horario en el Service */
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

    public List<Horario> obtenerHorariosPorMedico(String medicoId) { return horarioRepository.findByMedicoId(medicoId); }

    public void eliminarHorario(Integer id, String dia) {
        Optional<Horario> horario = horarioRepository.findById(id.toString());
        if (horario.isPresent() && horario.get().getDia().equalsIgnoreCase(dia)) {
            horarioRepository.delete(horario.get());
        }
    }
}