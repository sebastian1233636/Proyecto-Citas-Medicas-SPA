package org.example.proyecto2backend.presentation.Login;


import lombok.AllArgsConstructor;
import org.example.proyecto2backend.Security.TokenService;
import org.example.proyecto2backend.data.RolRepository;
import org.example.proyecto2backend.data.UsuarioRepository;
import org.example.proyecto2backend.logic.DTOs.LoginDTO;
import org.example.proyecto2backend.logic.DTOs.LoginResponse;
import org.example.proyecto2backend.logic.DTOs.RegistroDTO;
import org.example.proyecto2backend.logic.Rol;
import org.example.proyecto2backend.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class controller {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private final RolRepository rolRepository;



    @PostMapping("/register")
    public Usuario register(@RequestBody RegistroDTO dto) {
        Rol rol = rolRepository.findById(String.valueOf(dto.rolId())).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        Usuario usuario = new Usuario();
        usuario.setId(dto.id());
        usuario.setRol(rol);
        usuario.setNombre(dto.nombre());
        usuario.setClave(passwordEncoder.encode(dto.clave()));
        return usuarioRepository.save(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.id(), dto.clave()));

            String token = tokenService.generateToken(authentication);
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

}
