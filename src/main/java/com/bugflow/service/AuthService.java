package com.bugflow.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.bugflow.dto.LoginRequest;
import com.bugflow.dto.SignupRequest;
import com.bugflow.entity.Role;
import com.bugflow.entity.User;
import com.bugflow.exception.BadRequestException;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.UserRepository;
import com.bugflow.security.JwtUtil;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Environment env;

    public User signup(SignupRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        //  FIXED
        user.setRole(Role.Owner); 

        return userRepository.save(user);
    }

        public Map<String, Object> login(LoginRequest request) {

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found"));

            if (!passwordEncoder.matches(
                    request.getPassword(),
                    user.getPassword())) { 

                throw new BadRequestException("Invalid password");
            }

            String token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getRole().name()
            );

            Map<String, Object> response = new HashMap<>();

            response.put("token", token);

            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole()); 
            response.put(
            		"organizationId",
            		user.getOrganizationId());

            return response;
        }

        public Map<String, Object> loginWithAuth0(String accessToken) {

            String domain = env.getProperty("AUTH0_DOMAIN");
            if (domain == null || domain.isBlank()) {
                domain = env.getProperty("auth0.domain");
            }
            if (domain == null || domain.isBlank()) {
                throw new RuntimeException("AUTH0_DOMAIN environment variable or auth0.domain property must be set");
            }
            // Validate token using Auth0 JWKS (RS256)
            String expectedAudience = env.getProperty("AUTH0_AUDIENCE");
            if (expectedAudience == null || expectedAudience.isBlank()) {
                expectedAudience = env.getProperty("auth0.audience");
            }
            try {
                // Decode to get kid
                DecodedJWT decoded = JWT.decode(accessToken);
                String kid = decoded.getKeyId();

                // Build canonical issuer and JWKS URL. Support when AUTH0_DOMAIN may include scheme.
                String issuer = domain.startsWith("http://") || domain.startsWith("https://") ? domain : ("https://" + domain);
                if (!issuer.endsWith("/")) {
                    issuer = issuer + "/";
                }
                String jwksUrl = issuer + ".well-known/jwks.json";

                JwkProvider jwkProvider = new UrlJwkProvider(new java.net.URL(jwksUrl));
                Jwk jwk = jwkProvider.get(kid);

                java.security.interfaces.RSAPublicKey publicKey = (java.security.interfaces.RSAPublicKey) jwk.getPublicKey();
                Algorithm algorithm = Algorithm.RSA256(publicKey, null);

                JWTVerifier verifier;
                if (expectedAudience != null && !expectedAudience.isBlank()) {
                    verifier = JWT.require(algorithm)
                            .withAudience(expectedAudience)
                            .withIssuer(issuer)
                            .build();
                } else {
                    verifier = JWT.require(algorithm)
                            .withIssuer(issuer)
                            .build();
                }
                DecodedJWT verified = verifier.verify(accessToken);

                log.info("Auth0 JWT verification succeeded for subject: {}", verified.getSubject());

                String email = null;
                String name = null;
                try {
                    email = verified.getClaim("email").asString();
                    name = verified.getClaim("name").asString();
                } catch (Exception e) {
                    // ignore, we'll fetch profile from /userinfo if needed
                }

                if (email == null || email.isBlank()) {
                    // Access tokens for custom APIs may not include profile claims. Use /userinfo.
                    String userinfoUrl = issuer + "userinfo"; // issuer ends with '/'
                    log.info("Fetching Auth0 userinfo from {} for subject {}", userinfoUrl, verified.getSubject());
                    try {
                        RestTemplate rest = new RestTemplate();
                        HttpHeaders headers = new HttpHeaders();
                        headers.setBearerAuth(accessToken);
                        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                        HttpEntity<String> entity = new HttpEntity<>(headers);

                        ResponseEntity<Map> resp = rest.exchange(userinfoUrl, HttpMethod.GET, entity, Map.class);
                        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                            Map body = resp.getBody();
                            Object eObj = body.get("email");
                            Object nObj = body.get("name");
                            if (eObj != null) {
                                email = eObj.toString();
                            }
                            if (nObj != null) {
                                name = nObj.toString();
                            }
                            log.info("Auth0 userinfo returned profile for subject {}: email present={}", verified.getSubject(), email != null);
                        } else {
                            log.warn("Failed to fetch /userinfo: status={}", resp.getStatusCodeValue());
                        }
                    } catch (Exception exUser) {
                        log.warn("Exception while calling /userinfo: {}", exUser.getMessage());
                    }
                }

                if (email == null || email.isBlank()) {
                    throw new RuntimeException("Auth0 token verification succeeded but no email claim present");
                }

                // Proceed with finding/creating local user

                User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setName(name != null ? name : email);
                user.setPassword(null);
                // follow existing signup default
                user.setRole(com.bugflow.entity.Role.Owner);
                user = userRepository.save(user);
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("token", token);
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            response.put("organizationId", user.getOrganizationId());

            return response;
           } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(
                    "Auth0 token validation failed: " + ex.getMessage(),
                    ex
                );
            }
        }
}