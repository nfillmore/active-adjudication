package demo.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by cengruilin on 2018/1/30.
 */
public class CreateUserRequest {
    @NotNull
    private String username;
    @NotNull
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
