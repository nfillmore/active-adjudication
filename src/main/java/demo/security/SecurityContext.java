package demo.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * Created by cengruilin on 2017/12/22.
 */
public class SecurityContext {
    public static String currentUserId() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (null == user) {
            return null;
        }

        if (user instanceof User) {
            return ((User) user).getUsername();
        }

        return null;
    }
}
