package com.kerneldc.education.studentNotesService.security.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.kerneldc.education.studentNotesService.security.service.TokenAuthenticationService;

@Component
public class CrossOriginResourceSharingFilter extends GenericFilterBean {

    private static final String CLIENT_URL = "http://127.0.0.1:4200";
    private static final String ORIGIN_HEADER = "ORIGIN";

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        if (request.getHeader(ORIGIN_HEADER) != null
                && originEqualsClientUrl(request.getHeader(ORIGIN_HEADER), CLIENT_URL)) {
            setAccessControlHeader((HttpServletResponse) servletResponse, request.getHeader(ORIGIN_HEADER));
        }

        if (!HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private void setAccessControlHeader(final HttpServletResponse response, final String origin) {
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Origin, Accept, " +
                "Access-Control-Allow-Headers, Access-Control-Request-Headers, " + TokenAuthenticationService.AUTH_HEADER_NAME);
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    boolean originEqualsClientUrl(final String origin, final String clientUrl) {
        String adjustedOrigin = switchLocalhost(origin);
        if (adjustedOrigin.endsWith("/")) {
            adjustedOrigin = adjustedOrigin.substring(0, adjustedOrigin.length()-1);
        }

        return clientUrl.equalsIgnoreCase(adjustedOrigin);
    }

    String switchLocalhost(final String origin) {
        String result = origin;
        if (origin.toLowerCase().contains("localhost")) {
            // (?i) means: replaceAllIgnoreCase
            result = origin.replaceAll("(?i)localhost", "127.0.0.1");
        }

        return result;
    }
}
