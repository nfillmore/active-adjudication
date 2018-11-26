package demo.web.rest;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by cengruilin on 2017/11/1.
 */
@RestController
@RequestMapping("/api")
public class ApiRestController {

    @RequestMapping(path = "/everything", produces = "application/json")
    public String everything() throws IOException {
        return Resources.toString(Resources.getResource("everything.json"), Charsets.UTF_8);
    }
}
