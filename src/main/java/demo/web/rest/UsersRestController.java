package demo.web.rest;

import demo.dto.CreateUserRequest;
import demo.dto.ResetUserPasswordRequest;
import demo.dto.UserSummary;
import demo.security.domain.Role;
import demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by cengruilin on 2018/1/30.
 */
@RestController
@RequestMapping("/api/mgr/users")
public class UsersRestController {
    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private UserService userService;

    @GetMapping("")
    public List<UserSummary> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public void create(@Validated CreateUserRequest request) {
        Collection<GrantedAuthority> authorities = Arrays.asList(new Role[]{Role.USER});
        User user = new User(request.getUsername(), request.getPassword(), authorities);
        userDetailsManager.createUser(user);
    }

    @PostMapping("/{username}/resetPass")
    public void resetPassword(@PathVariable String username, @Validated ResetUserPasswordRequest request) {
        userService.resetPassword(username, request.getPassword());
    }

    @DeleteMapping("/{username}")
    public void delete(@PathVariable String username) {
        userDetailsManager.deleteUser(username);
    }
}
