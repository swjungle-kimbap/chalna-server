package com.jungle.chalnaServer.global.util;

import com.jungle.chalnaServer.domain.member.domain.dto.MemberInfo;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
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
import java.util.Optional;

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
        Member member = memberRepository.findById(getId(refreshToken)).orElseThrow(MemberNotFoundException::new);
        if (!member.getRefreshToken().equals(refreshToken) || isExpired(refreshToken)) {
            return null;
        }

        String newAccessToken = createAccessToken(member);
        String newRefreshToken = createRefreshToken(member);

        member.updateRefreshToken(newRefreshToken);

        return new Tokens(newAccessToken, newRefreshToken);
    }
    public String createAccessToken(Member member){
        return createToken(member,accessTokenExpirationTime);
    }

    public String createRefreshToken(Member member){
        return createToken(member, refreshTokenExpirationTime);
    }

    public String createToken(Member member,long expirationTime){
        long now = new Date(System.currentTimeMillis()).getTime();

        Claims claims = Jwts.claims();
        claims.put("id",member.getId());
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
        Optional<Member> findMember = memberRepository.findById(id);
        return findMember.isPresent();
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
