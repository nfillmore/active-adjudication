package demo.web.rest;

import demo.dto.TaskSummary;
import demo.dto.UserSummary;
import demo.service.TaskService;
import demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by cengruilin on 2018/3/15.
 */
@RestController
@RequestMapping("/api/shared")
public class SharedRestController {
    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @GetMapping("/users")
    public List<UserSummary> findAll() {
        return userService.findAll();
    }

    @GetMapping("/tasks")
    public List<TaskSummary> findAllTasks() {
        return taskService.listAll();
    }
}
