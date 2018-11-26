package demo.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by cengruilin on 2018/2/6.
 */
public class ResetUserPasswordRequest {
    @NotNull
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
