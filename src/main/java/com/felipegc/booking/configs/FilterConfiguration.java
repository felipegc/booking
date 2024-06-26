package com.felipegc.booking.configs;

import com.felipegc.booking.filters.AuthorizationFilter;
import com.felipegc.booking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {
    private final UserService userService;

    @Autowired
    public FilterConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilter() {
        FilterRegistrationBean<AuthorizationFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AuthorizationFilter(userService));

        registrationBean.addUrlPatterns("/properties/*");
        registrationBean.setOrder(1);
        // change if the filter should be disabled.
        registrationBean.setEnabled(true);

        return registrationBean;
    }
}
