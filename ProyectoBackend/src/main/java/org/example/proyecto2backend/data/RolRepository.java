package org.example.proyecto2backend.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.proyecto2backend.logic.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, String> { }