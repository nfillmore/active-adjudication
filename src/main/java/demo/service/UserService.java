package demo.service;

import demo.dto.UserSummary;

import java.util.List;

/**
 * Created by cengruilin on 2018/1/30.
 */
public interface UserService {
    List<UserSummary> findAll();

    void resetPassword(String username, String password);
}
