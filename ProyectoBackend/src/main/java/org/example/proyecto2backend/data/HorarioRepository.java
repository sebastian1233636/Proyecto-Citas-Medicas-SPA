package org.example.proyecto2backend.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.example.proyecto2backend.logic.Horario;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario,String> {
    List<Horario> findByMedicoId(@Param("medicoId") String medicoId);
    List<Horario> findByMedicoIdAndDia(String medicoId, String dia);
}