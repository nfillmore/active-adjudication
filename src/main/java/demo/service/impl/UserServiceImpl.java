package demo.service.impl;

import demo.dto.UserSummary;
import demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by cengruilin on 2018/1/30.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<UserSummary> findAll() {
        return jdbcTemplate.query("select * from users", new BeanPropertyRowMapper<>(UserSummary.class));
    }

    @Override
    public void resetPassword(String username, String password) {
        jdbcTemplate.update("update users set password=? where username=?", new Object[]{
                password, username
        });
    }
}
