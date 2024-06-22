package com.jungle.chalnaServer.global.auth.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jungle.chalnaServer.global.auth.jwt.CustomUserDetailsService;
import com.jungle.chalnaServer.global.auth.jwt.dto.Tokens;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.global.exception.GlobalErrorCode;
import com.jungle.chalnaServer.global.util.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    private final CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper objectMapper;

    private static final List<String> AUTHENTICATE_LIST = List.of(
            "/api/v1/auth/signup","/api/v1/auth/login","/health-check"
    );
    private static final List<String> AUTHENTICATE_PREFIX_LIST = List.of(
            "/images/","/uploads/", "/ws"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        for (String prefix : AUTHENTICATE_PREFIX_LIST){
            if (uri.startsWith(prefix)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        if (AUTHENTICATE_LIST.contains(uri)) {
            filterChain.doFilter(request, response);
            return;
        }
        String access = jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER);
        String refresh = jwtService.resolveToken(request, JwtService.REFRESH_HEADER);

        if (!jwtService.isValidateToken(access)) {
            tokenInvalidException(response);
            return;
        }
        if (jwtService.isExpired(access)) {
            Tokens tokens = jwtService.regen(refresh);
            if (tokens == null) {
                tokenInvalidException(response);
                return;
            }
            response.setHeader(JwtService.AUTHORIZATION_HEADER, JwtService.BEARER_PREFIX + tokens.accessToken());
            response.setHeader(JwtService.REFRESH_HEADER, JwtService.BEARER_PREFIX + tokens.refreshToken());
            access = tokens.accessToken();
        }
        Long id = jwtService.getId(access);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(id.toString());

        if (userDetails != null) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }


    private void tokenInvalidException(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        CommonResponse<?> res = CommonResponse.ok(GlobalErrorCode.INVALID_TOKEN);
        objectMapper.writeValue(response.getOutputStream(), res);
    }
}
