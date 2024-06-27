package com.felipegc.booking.filters;

import com.felipegc.booking.services.UserService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.UUID;

@Order(1)
public class AuthorizationFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationFilter.class);

    UserService userService;

    public AuthorizationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilter (
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        HttpServletRequest req = (HttpServletRequest) request;

        String authorization = req.getHeader("Authorization");

        LOG.info("Checking authorization for token :{}", authorization);

        boolean isPostMethod = req.getMethod().equalsIgnoreCase("POST");

        if(isPostMethod && !userService.isUserAuthorized(UUID.fromString(authorization))) {
            LOG.error("Token {} is not authorized", authorization);
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "User is not authorized");
        } else {
            chain.doFilter(request, response);
        }
    }
}
