package org.example.proyecto2backend.Security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public class UserInfo {
    public static String getUsuarioId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) { return jwt.getClaimAsString("id"); }
        return null;
    }

    public static String getNombreUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) { return jwt.getClaimAsString("name"); }
        return null;
    }

    public static String getRolUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) { return jwt.getClaimAsString("rol"); }
        return null;
    }
}
