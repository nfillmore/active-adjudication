package demo.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by cengruilin on 2017/12/7.
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login.html");
//        registry.addViewController("/").setViewName("/datatables_app/index.html");
//        registry.addRedirectViewController("/", "/datatables_app/index.html");
        registry.addRedirectViewController("/", "/task/list.html");
    }

}
