package com.cmc.utils;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    @Value("${jwt.secret}")
    private static String SECRET;
    @Value("${jwt.expiration}")
    private static long EXPIRATION;

    // 签名算法
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    private static Key getKey() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET);
        return new SecretKeySpec(apiKeySecretBytes, SIGNATURE_ALGORITHM.getJcaName());
    }

    // 生成 token
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SIGNATURE_ALGORITHM, getKey())
                .compact();
    }

    // 解析 token
    public static String getUsername(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getKey())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
