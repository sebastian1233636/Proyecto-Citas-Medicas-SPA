package org.example.proyecto2backend.data;

import org.example.proyecto2backend.logic.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioRepository extends JpaRepository<Horario,String> {
}
