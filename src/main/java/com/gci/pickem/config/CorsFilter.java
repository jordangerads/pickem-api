package com.gci.pickem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CorsFilter extends org.springframework.web.filter.CorsFilter {
    private static final Logger log = LoggerFactory.getLogger(CorsFilter.class);

    @Autowired
    public CorsFilter(CorsConfigurationSource configSource) {
        super(configSource);
        log.info("I'm Alive!");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("I'm here!");

        log.info(request.getRequestURI());

        super.doFilterInternal(request, response, filterChain);
    }
}
