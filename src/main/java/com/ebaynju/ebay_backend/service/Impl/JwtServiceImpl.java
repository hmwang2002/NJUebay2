package com.ebaynju.ebay_backend.service.Impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cardigan
 * @version 1.0
 * Create by 2022/11/26
 */

@Service
public class JwtServiceImpl {
    private static final Long EXPIRE_MINUTE = (long) 60 * 1000;
    private static final Long EXPIRE_HOUR =  EXPIRE_MINUTE * 60;
    private static final Long EXPIRE_DAY = EXPIRE_HOUR * 24;
    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(Map<String, Object> map) {
        Date date = new Date();
        // 设置有效期 (可以用上面的常量更改)
        date.setTime(System.currentTimeMillis() + EXPIRE_HOUR);
        String userId = Integer.toString((Integer) map.get("userId"));

        return Jwts.builder().setExpiration(date).setClaims(map).setAudience(userId).signWith(key).compact();
    }

    public Map<String, Object> getInfo(String jwt, String info) {
        Jws<Claims> jws = Jwts.parserBuilder().
                setSigningKey(key).
                build().
                parseClaimsJws(jwt);

        Claims claims = jws.getBody();

        Map<String, Object> map = new HashMap<>();

        map.put(info, claims.get(info, Object.class));

        return map;
    }
}
