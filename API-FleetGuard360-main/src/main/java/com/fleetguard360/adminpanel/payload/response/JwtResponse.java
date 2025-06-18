/*package com.fleetguard360.adminpanel.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Respuesta con el token JWT y datos del usuario autenticado")
public class JwtResponse {
    @Schema(description = "Token JWT para autenticación")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer", defaultValue = "Bearer")
    private String type = "Bearer";

    @Schema(description = "ID del usuario")
    private Long id;

    @Schema(description = "Nombre de usuario")
    private String username;

    @Schema(description = "Correo electrónico del usuario")
    private String email;

    @Schema(description = "Roles del usuario")
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}*/

/*package com.fleetguard360.adminpanel.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Respuesta con el token JWT y datos del usuario autenticado")
public class JwtResponse {

    @Schema(description = "Token JWT para autenticación")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String type;

    @Schema(description = "ID del usuario")
    private Long id;

    @Schema(description = "Nombre de usuario")
    private String username;

    @Schema(description = "Correo electrónico del usuario")
    private String email;

    @Schema(description = "Roles del usuario")
    private List<String> roles;

    // Constructor explícito que coincide con los parámetros usados en el controlador
    public JwtResponse(String token, String type, Long id, String username, String email, List<String> roles) {
        this.token = token;
        this.type = type;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}*/

package com.fleetguard360.adminpanel.payload.response;

import java.util.List;

public class JwtResponse {

    private String token;
    private String type;
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse() {}

    public JwtResponse(String token, String type, Long id, String username, String email, List<String> roles) {
        this.token = token;
        this.type = type;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}




