package dto;

public class AdminRequest {
    private String correo;
    private String password;

    // Getters
    public String getCorreo() {
        return correo;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
