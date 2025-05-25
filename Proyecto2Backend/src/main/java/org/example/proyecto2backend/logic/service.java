package org.example.proyecto2backend.logic;


import org.example.proyecto2backend.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
