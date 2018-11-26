package demo;

import demo.security.domain.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by cengruilin on 2017/8/11.
 */
@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartup.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!userDetailsManager.userExists("admin")) {
            Collection<GrantedAuthority> authorities = Arrays.asList(new Role[]{Role.ADMIN, Role.USER});
            User admin = new User("admin", "admin", authorities);
            userDetailsManager.createUser(admin);
        }

        if (!userDetailsManager.userExists("demo")) {
            Collection<GrantedAuthority> authorities = Arrays.asList(new Role[]{Role.USER});
            User demo = new User("demo", "changeit", authorities);
            userDetailsManager.createUser(demo);
        }
    }
}
