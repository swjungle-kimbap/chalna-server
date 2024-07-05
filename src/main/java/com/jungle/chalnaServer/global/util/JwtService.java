package com.jungle.chalnaServer.global.util;

import com.jungle.chalnaServer.domain.auth.domain.entity.AuthInfo;
import com.jungle.chalnaServer.domain.auth.repository.AuthInfoRepository;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.global.auth.jwt.dto.Tokens;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JwtService {
    public static final String BEARER_TYPE = "Bearer";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "Authorization_Refresh";
    public static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${jwt.refresh-token-expiration-time}")
    private long refreshTokenExpirationTime;

    private Key key;

    private final AuthInfoRepository authInfoRepository;
    private final MemberRepository memberRepository;

    @PostConstruct
    public void init(){
        log.info("key created");
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Tokens regen(String refreshToken){
        if(!isValidateToken(refreshToken)) {
            return null;
        }
        AuthInfo authInfo = authInfoRepository.findById(getId(refreshToken));
        if(authInfo == null || !authInfo.refreshToken().equals(refreshToken) || isExpired(refreshToken))
            return null;

        String newAccessToken = createAccessToken(authInfo.id(), authInfo.deviceId());
        String newRefreshToken = createRefreshToken(authInfo.id(), authInfo.deviceId());


        return new Tokens(newAccessToken, newRefreshToken);
    }
    public String createAccessToken(Long id,String deviceId){
        return createToken(id,deviceId,accessTokenExpirationTime);
    }

    public String createRefreshToken(Long id,String deviceId){
        return createToken(id,deviceId, refreshTokenExpirationTime);
    }

    public String createToken(Long id,String deviceId,long expirationTime){
        long now = new Date(System.currentTimeMillis()).getTime();

        Claims claims = Jwts.claims();
        claims.put("id",id);
        claims.put("deviceId",deviceId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public String resolveToken(HttpServletRequest request, String header) {
        String token = request.getHeader(header);
        if(token == null || token.trim().isEmpty())
            return null;
        return token.substring(7);
    }
    public boolean isValidateToken(String token) {
        if(token == null || token.trim().isEmpty())
            return false;
        Long id = getId(token);
        String deviceId = getDeviceId(token);
        AuthInfo authInfo = authInfoRepository.findById(id);
        return authInfo != null && authInfo.deviceId().equals(deviceId);
    }
    public String getDeviceId(String token){
        try {
            return getClaims(token).get("deviceId").toString();
        }
        catch (Exception e){
            return "";
        }
    }
    public Long getId(String token){
        try {
            return Long.valueOf(getClaims(token).get("id").toString());
        }
        catch (Exception e){
            return -1L;
        }
    }
    public boolean isExpired(String token){
        Date expireDate = getClaims(token).getExpiration();
        return expireDate.before(new Date());
    }

    public Claims getClaims(String token){
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        }
        catch (ExpiredJwtException e){
            return e.getClaims();
        }
        catch (SignatureException e){
            return null;
        }
    }
}
