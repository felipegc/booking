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

        String userId = req.getHeader("UserId");
        String authorization = req.getHeader("Authorization");

        LOG.info("Checking authorization for user {} with token :{}", userId, authorization);
        if(!userService.isUserAuthorized(userId, authorization)) {
            LOG.error("User {} is not authorized", userId);
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "User is not authorized");
        } else {
            chain.doFilter(request, response);
        }
    }
}
