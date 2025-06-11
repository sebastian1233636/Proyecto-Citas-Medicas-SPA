package org.example.proyecto2backend.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.proyecto2backend.logic.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Usuario findByNombreContainingIgnoreCase(String paciente);
}