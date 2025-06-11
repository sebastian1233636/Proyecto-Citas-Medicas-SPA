package org.example.proyecto2backend.presentation.Login;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.proyecto2backend.logic.DTOs.LoginResponse;
import org.example.proyecto2backend.data.UsuarioRepository;
import org.example.proyecto2backend.Security.TokenService;
import org.example.proyecto2backend.logic.DTOs.LoginDTO;
import org.springframework.security.core.Authentication;
import org.example.proyecto2backend.data.RolRepository;
import org.springframework.web.multipart.MultipartFile;
import org.example.proyecto2backend.logic.Usuario;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.example.proyecto2backend.logic.Rol;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import lombok.AllArgsConstructor;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController("loginController")
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
    public ResponseEntity<?> register( @RequestParam("id") String id, @RequestParam("clave") String clave,
            @RequestParam("nombre") String nombre, @RequestParam("rolId") String rolId,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        try {
            if (usuarioRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("El usuario ya está registrado.");
            }

            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            Usuario usuario = new Usuario();
            usuario.setId(id);
            usuario.setRol(rol);
            usuario.setNombre(nombre);
            usuario.setClave(passwordEncoder.encode(clave));

            Usuario guardado = usuarioRepository.save(usuario);

            if (imagen != null && !imagen.isEmpty()) {
                String nombreArchivo = id + ".jpg";
                String documentosDir = System.getProperty("user.home") + "/Documents/Usuarios/";

                Path directorioDestino = Paths.get(documentosDir);
                if (!Files.exists(directorioDestino)) {
                    Files.createDirectories(directorioDestino);
                }

                Path rutaArchivo = directorioDestino.resolve(nombreArchivo);
                imagen.transferTo(rutaArchivo.toFile());
            }

            return ResponseEntity.ok(guardado);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error de datos: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar el usuario: " + e.getMessage());
        }
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

    @GetMapping("/imagen/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable("id") String id) {
        try {
            Path pathImagen = Paths.get(System.getProperty("user.home") + "/Documents/Usuarios/" + id + ".jpg");

            if (Files.exists(pathImagen)) {
                byte[] imagenBytes = Files.readAllBytes(pathImagen);
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imagenBytes);
            }
        } catch (IOException e) {
        }

        return ResponseEntity.notFound().build();
    }
}