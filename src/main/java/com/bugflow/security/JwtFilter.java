package com.bugflow.security;
import com.bugflow.exception.OrganizationSuspendedException;
import org.springframework.http.MediaType;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Skip auth endpoints
        if (path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);
            String email = jwtUtil.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            	try {

            	    UserDetails userDetails =
            	            userDetailsService.loadUserByUsername(email);

            	    UsernamePasswordAuthenticationToken auth =
            	            new UsernamePasswordAuthenticationToken(
            	                    email,
            	                    null,
            	                    userDetails.getAuthorities()
            	            );

            	    auth.setDetails(
            	            new WebAuthenticationDetailsSource().buildDetails(request)
            	    );

            	    SecurityContextHolder.getContext().setAuthentication(auth);

            	}
            	catch (OrganizationSuspendedException ex) {

            	    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            	    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            	    response.getWriter().write(
            	            """
            	            {
            	                "status":403,
            	                "error":"Organization Suspended",
            	                "message":"Your organization has been suspended. Please contact BugFlow support."
            	            }
            	            """
            	    );

            	    return;
            	}
            }
        }

        filterChain.doFilter(request, response);
    }
}