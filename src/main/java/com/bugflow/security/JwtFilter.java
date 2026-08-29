package com.bugflow.security;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bugflow.exception.OrganizationSuspendedException;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

			try {
				String email = jwtUtil.extractEmail(token);

				if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					try {
						UserDetails userDetails = userDetailsService.loadUserByUsername(email);

						UsernamePasswordAuthenticationToken auth =
								new UsernamePasswordAuthenticationToken(
										email,
										null,
										userDetails.getAuthorities()
								);

						auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						SecurityContextHolder.getContext().setAuthentication(auth);

					} catch (OrganizationSuspendedException ex) {
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

			} catch (JwtException | IllegalArgumentException ex) {
				// Invalid/expired/stale JWT — do not fail the request.
				// Leave SecurityContext unauthenticated and continue the filter chain.
				// This ensures public endpoints like invitation acceptance remain accessible
				// even when the client sends a bad token.
				// Log at debug level if needed (avoid exposing details).
			}
		}

        filterChain.doFilter(request, response);
    }
}