package dev.szhuima.agent.trigger.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String secret;
    private final long expireMillis;
    private final Algorithm algorithm;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expireHours}") long expireHours) {
        this.secret = secret;
        // 小时转毫秒
        this.expireMillis = expireHours * 60 * 60 * 1000;
        this.algorithm = Algorithm.HMAC256(secret);
    }

    /**
     * 生成 token
     */
    public String generateToken(String subject) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + expireMillis);

        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(now)
                .withExpiresAt(expireAt)
                .sign(algorithm);
    }

    /**
     * 验证 token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /**
     * 获取 token 中的 subject
     */
    public String getSubject(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
