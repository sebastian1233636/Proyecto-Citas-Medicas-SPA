package org.example.proyecto2backend.Security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import org.example.proyecto2backend.logic.Usuario;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@AllArgsConstructor
public class TokenService {
    private final JwtConfig jwtConfig;
    public String generateToken(Authentication authentication) {
        var header = new JWSHeader.Builder(jwtConfig.getAlgorithm()).type(JOSEObjectType.JWT).build();
        Instant now = Instant.now();
        var builder = new JWTClaimsSet.Builder().issuer("TotalSoft").issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusMillis(jwtConfig.getJwtExpiration())));
        var scopes = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        builder.claim("scope", scopes);
        var user = (Usuario) authentication.getPrincipal();
        builder.claim("id", user.getId());
        builder.claim("name", user.getNombre());
        builder.claim("rol", user.getRol().getId());
        var claims = builder.build();
        var key = jwtConfig.getSecretKey();
        var jwt = new SignedJWT(header, claims);
        try { var signer = new MACSigner(key); jwt.sign(signer);
        } catch (JOSEException e) { throw new RuntimeException("Error generating JWT",e); }
        return jwt.serialize();
    }
}
