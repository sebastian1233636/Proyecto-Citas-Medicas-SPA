package org.example.proyecto2backend.data;

import org.example.proyecto2backend.logic.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, String> {

}
